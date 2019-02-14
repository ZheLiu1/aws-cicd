package com.howtodoinjava.rest.Service;

import com.howtodoinjava.rest.dao.INoteDAO;
import com.howtodoinjava.rest.model.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class NoteService implements INoteService {
    @Autowired
    INoteDAO noteDAO;

    @Override
    public int addNote(Note note){
        return noteDAO.addNote(note);
    };

    @Override
    public int addOwner(String id, String owner){
        return noteDAO.addOwner(id, owner);
    }

    @Override
    public Note findNoteById(String id){
        return noteDAO.findNoteById(id);
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
        List<String> ids = noteDAO.findIdByOwner(owner);
        Iterator<String> i = ids.iterator();
        while(i.hasNext()){
            notes.add(noteDAO.findNoteById(i.next()));
        }
        return notes;
    }

    @Override
    public int updateNote(Note account){
        return noteDAO.update(account);
    }

    @Override
    public int deleteNote(String id){
        return noteDAO.delete(id);
    }
}
