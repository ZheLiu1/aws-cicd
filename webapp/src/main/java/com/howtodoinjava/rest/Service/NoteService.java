package com.howtodoinjava.rest.Service;

import com.howtodoinjava.rest.dao.INoteDAO;
import com.howtodoinjava.rest.model.Attachment;
import com.howtodoinjava.rest.model.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class NoteService implements INoteService {
    @Autowired
    INoteDAO noteDAO;
    @Autowired
    private AmazonS3ClientService amazonS3ClientService;
    @Value("${aws.s3.audio.bucket}")
    private String domainName;
    //used to switch profile between dev/default
    @Value("${spring.datasource.username}")
    private String userName;

    @Override
    public int addNote(Note note){
        return noteDAO.addNote(note);
    };

    @Override
    public int addOwner(String id, String owner){
        return noteDAO.addOwner(id, owner);
    }

    @Override
    public Note findNoteById(String id, List<Attachment> list){
        Note note = findNoteOnlyById(id);
        note.setAttachments(list);
        return note;
    }

    @Override
    public Note findNoteOnlyById(String noteId){
        return noteDAO.findNoteById(noteId);
    }

    @Override
    public String findOwnerById(String id){
        return noteDAO.findOwnerById(id);
    }

    @Override
    public List<String> findIdByOwner(String owner){
        return noteDAO.findIdByOwner(owner);
    }

    @Override
    public List<Note> findNoteList(String owner){
        List<Note> notes = new ArrayList<>();
        List<String> ids = findIdByOwner(owner);
        if(ids != null) {
            Iterator<String> i = ids.iterator();
            while (i.hasNext()) {
                String noteId = i.next();
                List<Attachment> attachment = findAttachByNoteId(noteId);
                if (attachment == null)
                    notes.add(findNoteOnlyById(noteId));
                else
                    notes.add(findNoteById(noteId, attachment));
            }
        }
        return notes;
    }

    @Override
    public int updateNote(Note account){
        return noteDAO.update(account);
    }

    @Override
    public int deleteNote(String id){
        return noteDAO.deleteNote(id);
    }

    @Override
    public int deleteOwner(String id){
        return noteDAO.deleteOwner(id);
    }

    @Override
    public int deleteAttach(String id){
        Attachment attachment = findAttachById(id);
        if(attachment != null)
            deleteFile(attachment.getUrl());
        return noteDAO.deleteAttach(id);
    }

    @Override
    public void deleteAllAttach(String id){
        List<Attachment> list = findAttachByNoteId(id);
        if(list != null) {
            Iterator<Attachment> i = list.iterator();
            while (i.hasNext()) {
                deleteAttach(i.next().getId());
            }
        }
    }
    @Override
    public int addAttach(Attachment attachment, String noteId){
        return noteDAO.addAttach(attachment,noteId);
    }

    @Override
    public List<Attachment> findAttachByNoteId(String id){
        return noteDAO.findAttachByNoteId(id);
    }

    @Override
    public Attachment findAttachById(String id){
        return noteDAO.findAttachById(id);
    }

    @Override
    public Attachment findAttachByUrl(String url){
        return noteDAO.findAttachByUrl(url);
    }

    //delete file on disk
    private void deleteFile(String url){
        if(userName.equals("csye6225master")) {
            String fileName = url.replace("https://s3.amazonaws.com/" + domainName + "/", "");
            this.amazonS3ClientService.deleteFileFromS3Bucket(fileName);
        }else{
            File file = new File(url);
            if (file.isFile() && file.exists()) {
                file.delete();
            }
        }
    }
}
