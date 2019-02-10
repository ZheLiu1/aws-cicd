package com.howtodoinjava.rest.dao;

import com.howtodoinjava.rest.model.Note;

public interface INoteDAO {
    int addNote(Note note);

//    int updateNote(Note note);
//
//    int deleteNote(String id);
//
    Note findNoteById(String id);
//
//    List<Note> findNoteList();
}
