package com.howtodoinjava.rest.dao;

import com.howtodoinjava.rest.model.Note;

import java.util.List;


/**
 * Note interface for Database retrieval process
 */
public interface INoteDAO {
    int addNote(Note note);

    int addOwner(String id, String owner);

    Note findNoteById(String id);

    String findOwnerById(String id);

    List<String> findIdByOwner(String owner);

    int update(Note account);

    int deleteNote(String id);

    int deleteOwner(String id);

}
