package com.howtodoinjava.rest.dao;

import com.howtodoinjava.rest.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class UserDaoImpl implements IUserDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void createTables(String sql){
        jdbcTemplate.execute(sql);
    }
    //to add to the account
    @Override
    public int add(User account) {
        return jdbcTemplate.update("insert into user(user_id , user_name, user_password) values(?, ?, ?)",
                account.getUser_id(),
                account.getUser_name(),account.getUser_password());

    }

    @Override
    public int delete(int id) {
        return jdbcTemplate.update("DELETE from user where user_id=?",id);
    }

    @Override
    public User findAccountById(int id) {
        List<User> list = jdbcTemplate.query("select * from user where user_id = ?", new Object[]{id}, new BeanPropertyRowMapper(User.class));
        if(list!=null && list.size()>0){
            User account = list.get(0);
            return account;
        }else{
            return null;
        }
    }

    @Override
    public User findAccountByName(String name){
        List<User> list = jdbcTemplate.query("select * from user where user_name = ?", new Object[]{name}, new BeanPropertyRowMapper(User.class));
        if(list!=null && list.size()>0){
            User account = list.get(0);
            return account;
        }else{
            return null;
        }
    }

}
