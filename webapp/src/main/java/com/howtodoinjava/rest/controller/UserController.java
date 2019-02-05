package com.howtodoinjava.rest.controller;

import java.io.IOException;
import java.util.regex.Pattern;
import com.codahale.passpol.BreachDatabase;
import com.codahale.passpol.PasswordPolicy;


import com.howtodoinjava.rest.dao.IUserDAO;
import com.howtodoinjava.rest.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController
{
    @Autowired
    IUserDAO accountService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @RequestMapping(value = "/")
    public String httpGet(@RequestHeader(value="Authorization") String comingM){
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

    public boolean isValid(String email){

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);

        return pat.matcher(email).matches();


    }

    //Post request use of the change

    /**
     * Add user method for creating and registering the user
     * @param comingM: Json response to check and save the value.
     * @return
     */
    @RequestMapping(value = "/user/register")
    public String addUser(@RequestBody User comingM){

        String user_name = comingM.getUser_name();
        String user_password = comingM.getUser_password();


        // 1:: Duplicate username done
        User user= accountService.findAccountByName(user_name);
        if(user!=null && user.getUser_name().equalsIgnoreCase(user_name)){
            return "Duplicate Value enter again!!";
        }

        //2: : strong password code complying NIST
        final PasswordPolicy policy = new PasswordPolicy(BreachDatabase.haveIBeenPwned(5), 8, 64);

        //System.out.print("The message is:" +policy.check(user_password).toString());

        if(!policy.check(user_password).toString().equalsIgnoreCase("OK")){
            return "The Password is not Strong.Please change it according to NIST!";
        }

        // 3: check username for email in proper format
        if(isValid(user_name) && policy.check(user_password).toString().equalsIgnoreCase("OK")) {  //done
            User use = new User();

            int len=accountService.findAccountList().size()+16;
            use.setUser_id(len);
            use.setUser_name(user_name);
            use.setUser_password(bCryptPasswordEncoder.encode(user_password));

            accountService.add(use);

            return "Perfect!! KO. You have been registered.";
        }
        //4: check for the pattern of the Email address
        return "Wrong Email Pattern. Enter Again!!";
    }
}
