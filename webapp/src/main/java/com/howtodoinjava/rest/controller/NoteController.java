package com.howtodoinjava.rest.controller;

import com.howtodoinjava.rest.Service.AmazonS3ClientService;
import com.howtodoinjava.rest.Service.INoteService;
import com.howtodoinjava.rest.Service.IUserService;
import com.howtodoinjava.rest.exception.BadRequestException;
import com.howtodoinjava.rest.exception.NotFoundException;
import com.howtodoinjava.rest.exception.UnauthorizedException;
import com.howtodoinjava.rest.model.Attachment;
import com.howtodoinjava.rest.model.User;
import com.howtodoinjava.rest.model.Note;
import com.timgroup.statsd.StatsDClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.*;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;


/**
 * Note controller set for Note database which edits and sets the values under a user name
 */
@RestController
public class NoteController {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private IUserService accountService;
    @Autowired
    private INoteService noteService;
    @Autowired
    private AmazonS3ClientService amazonS3ClientService;
    //time format
    private java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //path of storage
    @Value("${localPath}")
    private String PATH;
    @Value("${aws.s3.audio.bucket}")
    private String domainName;
    //used to switch profile between dev/default
    @Value("${spring.datasource.username}")
    private String userName;
    @Autowired
    private StatsDClient statsDClient;

    private final static Logger logger = LoggerFactory.getLogger(NoteController.class);

    /**
     * Creating a note for a user
     * @param comingM
     * @param note
     * @return
     */
    @RequestMapping(value = "/note", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> addNote(@RequestHeader(value="Authorization") String comingM,
                                     @RequestBody Note note)
    {
        statsDClient.incrementCounter("endpoint.create.note.post");
        authen(comingM);

        if(note.getContent() == null || note.getTitle() == null) {
            logger.error("Title or content should not be empty");
            throw new BadRequestException("Title or content should not be empty");
        }

        String date = df.format(System.currentTimeMillis());
        //random generate a UUID for a note
        String uuid = UUID.randomUUID().toString();

        note.setId(uuid);
        note.setCreated_on(date);
        note.setLast_updated_on(date);
        noteService.addNote(note);
        String[] userInfo = UserController.decodeBase64(comingM);
        String user_name = userInfo[0];
        noteService.addOwner(uuid, user_name);

        logger.info("create a note success");
        return new ResponseEntity<>( note, HttpStatus.CREATED);
    }

    /**
     * Get note for a user with the provided id for the user
     * @param comingM: Auth code
     * @param id
     * @return
     */
    @RequestMapping(value = "/note/{id}", produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<?> addNote(@RequestHeader(value="Authorization") String comingM, @PathVariable("id") String id) {
        statsDClient.incrementCounter("endpoint.retrieve.note.get");
        authen(comingM);
        authrz(comingM, id);
        List<Attachment> attachment = noteService.findAttachByNoteId(id);
        Note note = noteService.findNoteById(id, attachment);
        if(note == null) {
            logger.error("the note required not exist");
            throw new NotFoundException("Not Found");
        }
        logger.info("info of note returned");
        return new ResponseEntity<>(note, HttpStatus.OK);
    }

    /**
     * Get all note for a particular user, will display all the notes for a particular user
     * @param comingM : value from the user base
     * @return
     */
    @RequestMapping(value = "/notesonia", produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<?> findAll(@RequestHeader(value="Authorization") String comingM) {
        statsDClient.incrementCounter("endpoint.retrieve.all.notes.get");
        authen(comingM);
        String[] userInfo = UserController.decodeBase64(comingM);
        String user_name = userInfo[0];
        //will save the notes under the list for a particular user
        List<Note> notes = noteService.findNoteList(user_name);
        if(notes == null || notes.size() == 0) {
            logger.error("No notes found");
            throw new NotFoundException("No notes found");
        }
        logger.info("all info of notes returned");
        return new ResponseEntity<>(notes, HttpStatus.OK);
    }

    /**
     * Update a note for a user
     * @param comingM: Auth code
     * @param id
     * @return
     */
    @RequestMapping(value = "/note/{id}", produces = "application/json", method = RequestMethod.PUT)
    public ResponseEntity<?> editNote(@RequestHeader(value="Authorization") String comingM, @RequestBody Note note , @PathVariable("id") String id) {
        statsDClient.incrementCounter("endpoint.update.note.put");
        //will check the authorization of the user
        authen(comingM);
        // will check if the body does not contain something
        if(note.getContent() == null || note.getTitle() == null) {
            logger.error("Title or content is empty");
            throw new BadRequestException("Title or content is empty");
        }

        authrz(comingM, id);
        note.setId(id);
        note.setLast_updated_on(df.format(System.currentTimeMillis()));
        noteService.updateNote(note);
        logger.info("note update success");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //delete a note
    @RequestMapping(value = "/note/{id}", produces = "application/json", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteNote(@RequestHeader(value="Authorization") String comingM, @PathVariable("id") String id) {
        statsDClient.incrementCounter("endpoint.delete.note.delete");
        authen(comingM);
        authrz(comingM,id);
        //delete
        noteService.deleteNote(id);
        noteService.deleteOwner(id);
        noteService.deleteAllAttach(id);

        logger.info("note & all attachemnts of it deleted");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //attach a file to a note
    @RequestMapping(value = "/note/{idNotes}/attachments", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> attachFile(@RequestHeader(value="Authorization") String comingM,
                                        @PathVariable("idNotes") String id,
                                        @RequestParam("file") MultipartFile file)throws Exception
    {
        statsDClient.incrementCounter("endpoint.upload.attachments.post");
        authen(comingM);
        authrz(comingM, id);
        String fileName = file.getOriginalFilename();
        String url = "https://s3.amazonaws.com/" + domainName + "/" + fileName;

        checkDuplicate(fileName);

        writeFile(file);

        Attachment attachment = new Attachment();
        attachment.setId(UUID.randomUUID().toString());
        if(userName.equals("csye6225master"))
            attachment.setUrl(url);
        else
            attachment.setUrl(PATH + fileName);

        Note note = noteService.findNoteOnlyById(id);
        if(note == null) {
            logger.error("note not found");
            throw new NotFoundException("note not found");
        }
        noteService.addAttach(attachment, note.getId());

        logger.info("attach success");
        return new ResponseEntity<>(attachment, HttpStatus.OK);
    }

    //Get list of files attached to the note
    @RequestMapping(value = "/note/{idNotes}/attachments", produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<?> attachAllFile(@RequestHeader(value="Authorization") String comingM,
                                        @PathVariable("idNotes") String id){
        statsDClient.incrementCounter("endpoint.retrieve.all.attachments.get");
        authen(comingM);
        authrz(comingM, id);
        List<Attachment> list = noteService.findAttachByNoteId(id);
        if(list == null) {
            logger.error("this user don't have any note");
            throw new NotFoundException("Not found");
        }
        logger.info("all info of attachments returned");
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    //Update file attached to the note
    @RequestMapping(value = "/note/{idNotes}/attachments/{idAttachments}", produces = "application/json", method = RequestMethod.PUT)
    public ResponseEntity<?> updateFile(@RequestHeader(value="Authorization") String comingM,
                                        @PathVariable("idNotes") String idNotes,
                                        @PathVariable("idAttachments") String idAttachments,
                                        @RequestParam("file") MultipartFile file)throws Exception
    {
        statsDClient.incrementCounter("endpoint.update.attachment.put");
        authen(comingM);
        authrz(comingM, idNotes);

        Attachment attachment = noteService.findAttachById(idAttachments);
        if(attachment == null) {
            logger.error("the attachment not found");
            throw new NotFoundException("the attachment not found");
        }

        //check if the attachment belongs to the note
        List<Attachment> list = noteService.findAttachByNoteId(idNotes);
        if(list == null) {
            logger.error("Not found the note");
            throw new NotFoundException("Not found the note");
        }
        Iterator<Attachment> i = list.iterator();
        boolean flag = false;
        while(i.hasNext()){
            Attachment attachment1 = i.next();
            if (attachment1.getId().equals(idAttachments) ){
                flag = true;
                break;
            }
        }
        if(!flag) {
            logger.error("the provided attachments do not belong to the note");
            throw new BadRequestException("the attachments do not belong to the note");
        }

        //replace attachments with the new one
        String fileName = file.getOriginalFilename();
        checkDuplicate(fileName);
        noteService.deleteAttach(idAttachments);

            writeFile(file);
        if(userName.equals("csye6225master"))
            attachment.setUrl("https://s3.amazonaws.com/" + domainName + "/" + fileName);
        else
            attachment.setUrl(PATH + file.getOriginalFilename());
        noteService.addAttach(attachment, idNotes);

        logger.info("update success");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    //Delete file attached to the transaction
    @RequestMapping(value = "/note/{idNotes}/attachments/{idAttachments}", produces = "application/json", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteFile(@RequestHeader(value="Authorization") String comingM,
                                        @PathVariable("idNotes") String idNotes,
                                        @PathVariable("idAttachments") String idAttachments){
        statsDClient.incrementCounter("endpoint.delete.attachment.delete");
        authen(comingM);
        authrz(comingM, idNotes);
        noteService.deleteAttach(idAttachments);

        logger.info("delete success");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //upload to S3 or write file to disk
    private void writeFile(MultipartFile file)throws IOException{
        if(userName.equals("csye6225master"))
            this.amazonS3ClientService.uploadFileToS3Bucket(file, true);
        else{
            String fileName = file.getOriginalFilename();
            byte[] bytes = file.getBytes();
            Path path = Paths.get(PATH , fileName);
            Files.write(path, bytes);
        }
        logger.info("upload attachment success");

    }

    //check if exist the same file name
    private void checkDuplicate(String fileName){
        Attachment temp;
        if(userName.equals("csye6225master"))
            temp = noteService.findAttachByUrl("https://s3.amazonaws.com/" + domainName + "/" + fileName);
        else
            temp = noteService.findAttachByUrl(PATH + fileName);
        if(temp != null) {
            logger.error("This file name has exist, try a new one");
            throw new BadRequestException("This file name has exist");
        }
        logger.info("check file name success");
    }

    //user authentication
    private void authen(String comingM){
        String[] userInfo = UserController.decodeBase64(comingM);
        String user_name = userInfo[0];
        String user_password = userInfo[1];

        User user = accountService.findAccountByName(user_name);
        if(user != null && bCryptPasswordEncoder.matches(user_password, user.getUser_password()) ) {
            logger.info("user authentication success");
            return;
        }
        else {
            logger.error("User Unauthorized");
            throw new UnauthorizedException("User Unauthorized");
        }
    }

    //user authorization
    private void authrz(String comingM, String id){
        String owner = noteService.findOwnerById(id);
        if(owner == null) {
            logger.error("Not Found the note");
            throw new NotFoundException("Not Found the note");
        }
        String[] userInfo = UserController.decodeBase64(comingM);
        String user_name = userInfo[0];
        if(!owner.equals(user_name)) {
            logger.error("User Unauthorized");
            throw new UnauthorizedException("User Unauthorized");
            //  throw new ForbiddenException("The user can not access the note");
        }
        logger.info("user authorization success");
    }
}
