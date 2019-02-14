package com.howtodoinjava.rest.Service;

import com.howtodoinjava.rest.dao.INoteDAO;
import com.howtodoinjava.rest.model.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoteService implements INoteService {
    @Autowired
    INoteDAO noteDAO;

    @Override
    public int addNote(Note note){
        return noteDAO.addNote(note);
    }

    @Override
    public Note findNoteById(String id){
        return noteDAO.findNoteById(id);
    }

}
