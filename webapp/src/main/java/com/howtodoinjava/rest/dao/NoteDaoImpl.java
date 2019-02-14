package com.howtodoinjava.rest.dao;

import com.howtodoinjava.rest.model.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NoteDaoImpl implements INoteDAO{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int addNote(Note note){
        return jdbcTemplate.update("insert into note(id , content, title, created_on, last_updated_on, owner) values(?, ?, ?, ?, ?, ?)",
                note.getId(),
                note.getContent(),
                note.getTitle(),
                note.getCreated_on(),
                note.getLast_updated_on(),
                note.getOwner());
    }

    @Override
    public Note findNoteById(String id){
        List<Note> list = jdbcTemplate.query("select * from note where id = ?", new Object[]{id}, new BeanPropertyRowMapper(Note.class));
        if(list!=null && list.size()>0){
            Note account = list.get(0);
            return account;
        }else{
            return null;
        }
    }


    @Override
    public List<Note> findAllNote(String user){
        List<Note> list = jdbcTemplate.query("select * from note where owner = ?", new Object[]{user}, new BeanPropertyRowMapper(Note.class));
        if(list!=null && list.size()>0) {
        return list;
        }
        return null;
    }



    @Override
    public int update(Note account) {
        return jdbcTemplate.update("UPDATE  user SET user_name=? ,user_password=? WHERE user_id=?",
                account.getTitle(),account.getContent(),account.getId());
    }

}

