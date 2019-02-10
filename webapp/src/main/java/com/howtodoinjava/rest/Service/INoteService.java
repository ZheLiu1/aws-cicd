package com.howtodoinjava.rest.Service;

import com.howtodoinjava.rest.model.Note;

public interface INoteService {
    int addNote(Note note);

//    int updateNote(Note note);
//
//    int deleteNote(String id);
//
    Note findNoteById(String id);
//
//    List<Note> findNoteList();
}
