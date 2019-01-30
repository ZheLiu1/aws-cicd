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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.annotation.Resource;

@RestController
public class EmployeeController 
{
    @Autowired
    IUserDAO accountService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @RequestMapping(value = "/")
    public String hello(@RequestHeader(value="Authorization") String comingM){
        String[] userInfo = decodeBase64(comingM);
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

    private boolean verify(String user_name, String user_password){
        boolean result = false;

        User user = accountService.findAccountByName(user_name);
        if(user != null && bCryptPasswordEncoder.matches(user_password, user.getUser_password()) )
        //if( bCryptPasswordEncoder.matches(user_password, "$2a$10$KwffF28hREFYPTtJ7FCguOzc2CBNSzWAICAm4XfDIsAQX0ZKWosSe") )
            result = true;
        return result;
    }

    private String[] decodeBase64(String comimgM){
        String[] pureM = comimgM.split(" ");
        sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
        String temp = null;
        try{
            temp = new String(decoder.decodeBuffer(pureM[1]));
        }catch(IOException e){
            e.printStackTrace();
        }
        String[] userInfo = temp.split(":");
        return userInfo;
    }

    //@GetMapping(path="/test", produces = "application/json")
    //public String getBcrypt(){
        //return bCryptPasswordEncoder.encode("123456");
        //if(bCryptPasswordEncoder.matches("123456","$2a$10$KwffF28hREFYPTtJ7FCguOzc2CBNSzWAICAm4XfDIsAQX0ZKWosSe"))
            //return "true";
        //return"false";
    //}

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
