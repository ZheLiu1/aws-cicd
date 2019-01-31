package com.howtodoinjava.rest;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import com.howtodoinjava.rest.dao.IUserDAO;
import com.howtodoinjava.rest.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import net.minidev.json.JSONObject;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JDBCFunctionTest {

    @Autowired
    IUserDAO userService;

    @Test
    @Transactional
    public void testAdd() {
        User user = new User();
        user.setUser_name("admin");
        user.setUser_password("test");
        user.setUser_id(10000);

        this.userService.add(user);
        Assert.assertEquals("User name is admin", "admin",
                userService.findAccountByName("admin").getUser_name());
    }

    @Test
    @Transactional
    public void testFindName() {
        User user = new User();
        user.setUser_name("admin");
        user.setUser_password("test");
        user.setUser_id(10000);

        this.userService.add(user);
        user = this.userService.findAccountByName("admin");
        Assert.assertEquals("User name is admin", "admin",
                user.getUser_name());
    }



    @Test
    @Transactional
    public void testUpdate() {
        User user = userService.findAccountByName("admin");
        user.setUser_name("admin_test");

        this.userService.update(user);
        Assert.assertEquals("Updated User name is admin_test", "admin_test",
                userService.findAccountByName("admin_test").getUser_name());
    }

    @Test
    @Transactional
    public void testFindId() {
        if(userService.findAccountById(1000) == null){
            User user = new User();
            user.setUser_name("admin");
            user.setUser_password("test");
            user.setUser_id(1000);
            userService.add(user);
        }


        Assert.assertEquals("User ID is 1000", 1000,
                userService.findAccountById(1000).getUser_id());
    }

    @Test
    @Transactional
    public void testDelete() {
        User user = null;
        if(userService.findAccountById(1000) == null){
            user = new User();
            user.setUser_name("admin");
            user.setUser_password("test");
            user.setUser_id(1000);
        }else
            user = userService.findAccountById(1000);
        userService.delete(1000);
        //userService.add(user);
        Assert.assertEquals("User ID of 1000 do not exist!", true,
                userService.findAccountById(1000)==null);
    }

}
