package com.howtodoinjava.rest.controller;

import com.howtodoinjava.rest.dao.IUserDAO;
import com.howtodoinjava.rest.exception.BadRequestException;
import com.howtodoinjava.rest.exception.ForbiddenException;
import com.howtodoinjava.rest.exception.NoteNotFoundException;
import com.howtodoinjava.rest.exception.UnauthorizedException;
import com.howtodoinjava.rest.model.User;
import com.howtodoinjava.rest.dao.INoteDAO;
import com.howtodoinjava.rest.model.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;


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
    private IUserDAO accountService;
    @Autowired
    private INoteDAO noteService;
    //time format
    private java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //used in Note as owner
    private String user_name;

    //Create a note for the user

    /**
     * Creating a note for a user
     * @param comingM
     * @param note
     * @return
     */
    @RequestMapping(value = "/note", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> addNote(@RequestHeader(value="Authorization") String comingM, @RequestBody Note note) {
        if(!ifAuthen(comingM))
            throw new UnauthorizedException("User Unauthorized");

        if(note.getContent() == null || note.getTitle() == null)
            throw new BadRequestException("Title or content should not be empty");

        String date = df.format(System.currentTimeMillis());
        //random generate a UUID for a note
        UUID uuid = UUID.randomUUID();

        note.setId(uuid.toString());
        note.setCreated_on(date);
        note.setOwner(user_name);
        note.setLast_updated_on("New Note");
        noteService.addNote(note);
        return new ResponseEntity<>( note, HttpStatus.CREATED);
    }

    //Get note for the user

    /**
     * Get note for a user with the provided id for the user
     * @param comingM: Auth code
     * @param id
     * @return
     */
    @RequestMapping(value = "/note/{id}", produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<?> addNote(@RequestHeader(value="Authorization") String comingM, @PathVariable("id") String id) {
        if (!ifAuthen(comingM))
            throw new UnauthorizedException("User Unauthorized");
        Note note = noteService.findNoteById(id);
        if(note == null)
            throw new NoteNotFoundException("Not Found");
        if(!note.getOwner().equals(user_name))
            throw new ForbiddenException("The user can not access the note");
        return new ResponseEntity<>(note, HttpStatus.OK);
    }

    //user authentication
    private Boolean ifAuthen(String comingM){
        String[] userInfo = UserController.decodeBase64(comingM);
        user_name = userInfo[0];
        String user_password = userInfo[1];

        boolean result = false;
        User user = accountService.findAccountByName(user_name);
        if(user != null && bCryptPasswordEncoder.matches(user_password, user.getUser_password()) )
            result = true;
        return result;
    }


//===========================================================================


    /**
     * Get all note for a particular user, will display all the notes for a particular user
     * @param comingM : value from the user base
     * @return
     */
    @RequestMapping(value = "/note", produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<?> findAll(@RequestHeader(value="Authorization") String comingM) {
        if (!ifAuthen(comingM))
            throw new UnauthorizedException("User Unauthorized");

        //will save the notes under the list for a particular user
        List<Note> not = noteService.findAllNote(user_name);
        if(not == null)
            throw new NoteNotFoundException("Not Found or you dont have access");
        if(!not.get(0).getOwner().equals(user_name))
            throw new ForbiddenException("The user can not access the note");
        return new ResponseEntity<>(not, HttpStatus.OK);
    }



    //Update a note for the user

    /**
     * Update a note for a user
     * @param comingM: Auth code
     * @param id
     * @return
     */

    @RequestMapping(value = "/editNote/{id}", produces = "application/json", method = RequestMethod.PUT)
    public ResponseEntity<?> editNote(@RequestHeader(value="Authorization") String comingM, @RequestBody Note note , @PathVariable("id") String id) {

        //will check the authorization of the user
        if(!ifAuthen(comingM))
            throw new UnauthorizedException("User Unauthorized");
        // will check if the body does not contain something
        if(note.getContent() == null || note.getTitle() == null)
            throw new BadRequestException("Title or content should not be empty");
        // updating the values of id, user_name, note and title from body


        Note prevNote=noteService.findNoteById(id);
        if(prevNote.getContent().contains(note.getContent())){

            return new ResponseEntity<>( "The content already there. Please change the contents", HttpStatus.ALREADY_REPORTED);

        }

        note.setId(id);
        note.setOwner(user_name);
        noteService.update(note);

        // return the new note created in database
        return new ResponseEntity<>( noteService.findNoteById(id), HttpStatus.CREATED);
    }



}
