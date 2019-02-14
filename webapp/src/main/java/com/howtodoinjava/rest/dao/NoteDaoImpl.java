package com.howtodoinjava.rest.dao;

import com.howtodoinjava.rest.model.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Database object class which has methods and usecases to update and check the database components for Note class
 */
@Repository
public class NoteDaoImpl implements INoteDAO{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int addNote(Note note){
        return jdbcTemplate.update("insert into note(id , content, title, created_on, last_updated_on) values(?, ?, ?, ?, ?)",
                note.getId(),
                note.getContent(),
                note.getTitle(),
                note.getCreated_on(),
                note.getLast_updated_on() );
    }

    @Override
    public int addOwner(String id, String owner){
        return jdbcTemplate.update("insert into owners(id, owner) values (?,?)", id, owner);
    }

    @Override
    public Note findNoteById(String id){
        List<Note> list = jdbcTemplate.query("select * from note where id = ?", new Object[]{id}, new BeanPropertyRowMapper(Note.class));
        if(list!=null && list.size()>0){
            Note account = list.get(0);
            return account;
        }else
            return null;
    }

    @Override
    public String findOwnerById(String id){
        final List<String> owners = new ArrayList<>();
        jdbcTemplate.query("select * from owners where id = ?",new Object[]{id},new RowCallbackHandler(){

            public void processRow(ResultSet rs) throws SQLException {
               owners.add(rs.getString("owner"));
            }
        });
        if(owners!=null && owners.size()>0){
            String owner = owners.get(0);
            return owner;
        }else
            return null;
    }

    @Override
    public List<String> findIdByOwner(String owner){
        final List<String> ids = new ArrayList<>();
        jdbcTemplate.query("select * from owners where owner = ?",new Object[]{owner},new RowCallbackHandler(){

            public void processRow(ResultSet rs) throws SQLException {
                ids.add(rs.getString("id"));
            }
        });
        if(ids!=null && ids.size()>0){
            return ids;
        }else
            return null;
    }

    /**
     * will update title, last updated on and content for the given input
     * @param account
     * @return
     */
    @Override
    public int update(Note account) {
        return jdbcTemplate.update("UPDATE  note SET title=?, content=? , last_updated_on=? WHERE id=?",
                account.getTitle(), account.getContent(), account.getLast_updated_on(), account.getId());
    }

    @Override
    public int deleteNote(String id) {
        return jdbcTemplate.update("DELETE from note where id=?",id);
    }

    @Override
    public int deleteOwner(String id) {
        return jdbcTemplate.update("DELETE from owners where id=?",id);
    }


    @Override
    public List<Note> findAllNote(String user){
        List<Note> list = jdbcTemplate.query("select * from note where owner = ?", new Object[]{user}, new BeanPropertyRowMapper(Note.class));
        if(list!=null && list.size()>0) {
        return list;
        }
        return null;
    }

//====================================================================
    /**
     * will update title, last updated on and content for the given input
     * @param account
     * @return
     */
    @Override
    public int update(Note account) {

        //to get current time for update time
         java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String date = df.format(System.currentTimeMillis());

        return jdbcTemplate.update("UPDATE  note SET title=?, content=? , last_updated_on=? WHERE id=?",
                account.getTitle(), account.getContent(), date, account.getId());
    }

    @Override
    public int deleteNote(String id) {
        return jdbcTemplate.update("DELETE FROM note WHERE id = ?", id);
    }
}

