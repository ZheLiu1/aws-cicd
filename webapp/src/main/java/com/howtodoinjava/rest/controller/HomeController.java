package com.howtodoinjava.rest.controller;

import com.howtodoinjava.rest.Service.IUserService;
import com.howtodoinjava.rest.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @Autowired
    IUserService accountService;

    @RequestMapping("/")
    public String home(){
        return "Welcome to course work of csye6225!";
    }

    @GetMapping("/test")
    public String ptest(@RequestParam("username") String username) {
        User user = accountService.findAccountByName(username);
        return user.getUser_name();
    }
}
