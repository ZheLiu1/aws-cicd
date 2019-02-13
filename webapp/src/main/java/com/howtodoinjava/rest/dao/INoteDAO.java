package com.howtodoinjava.rest.dao;

import com.howtodoinjava.rest.model.Note;

import java.util.List;


/**
 * Note interface for Database retrieval process
 */
public interface INoteDAO {
    int addNote(Note note);



    Note findNoteById(String id);


    List<Note> findAllNote(String user);


    int update(Note account);
}
