package com.howtodoinjava.rest;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.howtodoinjava.rest.dao.IUserDAO;
import com.howtodoinjava.rest.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

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

