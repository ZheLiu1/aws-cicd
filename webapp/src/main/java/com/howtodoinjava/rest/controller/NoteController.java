package com.howtodoinjava.rest.controller;

import com.howtodoinjava.rest.dao.INoteDAO;
import com.howtodoinjava.rest.dao.IUserDAO;
import com.howtodoinjava.rest.exception.NoteNotFoundException;
import com.howtodoinjava.rest.exception.UnauthorizedException;
import com.howtodoinjava.rest.model.Note;
import com.howtodoinjava.rest.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

import static com.howtodoinjava.rest.controller.UserController.decodeBase64;

@RestController
public class NoteController {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private IUserDAO accountService;
    @Autowired
    private INoteDAO noteService;
    //time format
    private java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //used in Note as owner
    private String user_name;

    //Create a note for the user
    @RequestMapping(value = "/note", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> addNote(@RequestHeader(value="Authorization") String comingM) {
        if(!ifAuthen(comingM))
            throw new UnauthorizedException("User Unauthorized");

        Note note = new Note();
        String date = df.format(System.currentTimeMillis());
        //random generate a UUID for a note
        UUID uuid = UUID.randomUUID();

        note.setId(uuid.toString());
        note.setCreated_on(date);
        note.setOwner(user_name);
        noteService.addNote(note);
        return new ResponseEntity<>( note, HttpStatus.CREATED);
    }

    //Get note for the user
    @RequestMapping(value = "/note/{id}", produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<?> addNote(@RequestHeader(value="Authorization") String comingM, @PathVariable("id") String id) {
        if (!ifAuthen(comingM))
            throw new UnauthorizedException("User Unauthorized");
        Note note = noteService.findNoteById(id);
        if(note == null)
            throw new NoteNotFoundException("Not Found");
        return new ResponseEntity<>(note, HttpStatus.OK);
    }

    //user authentication
    private Boolean ifAuthen(String comingM){
        String[] userInfo = decodeBase64(comingM);
        user_name = userInfo[0];
        String user_password = userInfo[1];

        boolean result = false;
        User user = accountService.findAccountByName(user_name);
        if(user != null && bCryptPasswordEncoder.matches(user_password, user.getUser_password()) )
            result = true;
        return result;
    }


}
