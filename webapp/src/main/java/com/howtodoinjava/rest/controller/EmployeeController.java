package com.howtodoinjava.rest.controller;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.howtodoinjava.rest.dao.IUserDAO;
import com.howtodoinjava.rest.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.annotation.Resource;

@RestController
@RequestMapping(path = "/users")
public class EmployeeController 
{
    @Autowired
    IUserDAO accountService;

    @RequestMapping(value = "/")
    public String hello(@RequestHeader(value="Authorization") String comingM){
        String[] pureM = comingM.split(" ");

        sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
        String temp = null;
        try{
            temp = new String(decoder.decodeBuffer(pureM[1]));

        }catch(IOException e){
            e.printStackTrace();
        }
        String[] userInfo = temp.split(":");
        String user_name = userInfo[0];
        String user_password = userInfo[1];
        String returnM = null;

        if(verify(user_name,user_password)){
            java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            returnM = df.format(System.currentTimeMillis());
        }else
            returnM = "You are not logged in!";
        return returnM;

    }

    public boolean verify(String user_name, String user_password){
        boolean result = false;

        //encode BCrypt

        //query from database, if find match, result = true
        User user = accountService.findAccountByName(user_name);
        if(user != null && user.getUser_password().equals(user_password))
            result = true;
        return result;
    }

    //@GetMapping(path="/test", produces = "application/json")
    //public String getUserById(){
    //    return accountService.findAccountById(1).getUser_name();
    //}

    //@GetMapping(path="/test", produces = "application/json")
    //public String getUserByName(){
     //   return accountService.findAccountByName("admin").getUser_password();
    //}

    /*
    @GetMapping(path="/test", produces = "application/json")
    public List<User> getUserList(ModelMap map){
        String sql = "SELECT * FROM user";
        List<User> userList = jdbcTemplate.query(sql, new RowMapper<User>(){
            User user = null;
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException{
                user = new User();
                user.setUser_id(rs.getString("user_id"));
                user.setUser_name(rs.getString("user_name"));
                user.setUser_password(rs.getString("user_password"));
                return user;
            }
        });
        return userList;
    }
    */


}
