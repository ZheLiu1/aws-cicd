package com.howtodoinjava.rest.Service;

import com.howtodoinjava.rest.model.Note;

import java.util.List;

public interface INoteService {
    int addNote(Note note);

    int addOwner(String id, String owner);

    int updateNote(Note note);

    int deleteNote(String id);

    Note findNoteById(String id);

    String findOwnerById(String id);

    List<String> findIdByOwner(String owner);

    List<Note> findNoteList(String owner);
}
