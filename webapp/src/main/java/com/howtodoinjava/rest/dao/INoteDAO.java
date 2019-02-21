package com.howtodoinjava.rest.dao;

import com.howtodoinjava.rest.model.Attachment;
import com.howtodoinjava.rest.model.Note;

import java.util.List;


/**
 * Note interface for Database retrieval process
 */
public interface INoteDAO {
    int addNote(Note note);

    int addOwner(String id, String owner);

    int addAttach(Attachment attachment, String noteId);

    int update(Note account);

    int deleteNote(String id);

    int deleteOwner(String id);

    int deleteAttach(String id);

    Note findNoteById(String id);

    String findOwnerById(String id);

    Attachment findAttachById(String id);

    Attachment findAttachByUrl(String url);

    List<String> findIdByOwner(String owner);

    List<Attachment> findAttachByNoteId(String id);
}
