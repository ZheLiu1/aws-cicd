package com.howtodoinjava.rest.Service;

import com.howtodoinjava.rest.model.Attachment;
import com.howtodoinjava.rest.model.Note;

import java.util.List;

public interface INoteService {
    int addNote(Note note);

    int addOwner(String id, String owner);

    int addAttach(Attachment attachment, String noteId);

    int updateNote(Note note);

    int deleteNote(String id);

    int deleteOwner(String id);

    int deleteAttach(String id);

    void deleteAllAttach(String id);

    Note findNoteById(String id, List<Attachment> list);

    Note findNoteOnlyById(String noteId);

    Attachment findAttachByUrl(String url);

    Attachment findAttachById(String id);

    String findOwnerById(String id);

    List<String> findIdByOwner(String owner);

    List<Note> findNoteList(String owner);

    List<Attachment> findAttachByNoteId(String id);
}
