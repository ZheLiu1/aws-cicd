package com.howtodoinjava.rest.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;
import com.codahale.passpol.BreachDatabase;
import com.codahale.passpol.PasswordPolicy;


import com.howtodoinjava.rest.Service.IUserService;
import com.howtodoinjava.rest.model.User;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController
{
    @Autowired
    IUserService accountService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private StatsDClient statsDClient;
    private String sql1 = "CREATE TABLE IF NOT EXISTS user(\n" +
            "user_id INT UNSIGNED AUTO_INCREMENT,\n" +
            "user_name VARCHAR(40) NOT NULL,\n" +
            "user_password VARCHAR(100) NOT NULL,\n" +
            "PRIMARY KEY ( `user_id` )\n" +
            ")ENGINE=InnoDB DEFAULT CHARSET=utf8;\n";

    private String sql2 ="CREATE TABLE IF NOT EXISTS note(\n" +
            "pid INT UNSIGNED AUTO_INCREMENT,\n" +
            "id VARCHAR(40) NOT NULL,\n" +
            "content VARCHAR(50) ,\n" +
            "title VARCHAR(40) ,\n" +
            "created_on VARCHAR(40) NOT NULL,\n" +
            "last_updated_on VARCHAR(40) ,\n" +
            "PRIMARY KEY ( `pid` )\n" +
            ")ENGINE=InnoDB DEFAULT CHARSET=utf8;\n";

    private String sql3 ="CREATE TABLE IF NOT EXISTS owners(\n" +
            "pid INT UNSIGNED AUTO_INCREMENT,\n" +
            "id VARCHAR(40) NOT NULL,\n" +
            "owner VARCHAR(40) NOT NULL,\n" +
            "PRIMARY KEY ( `pid` )\n" +
            ")ENGINE=InnoDB DEFAULT CHARSET=utf8;\n";


    private String sql4 ="CREATE TABLE IF NOT EXISTS attachment(\n" +
            "pid INT UNSIGNED AUTO_INCREMENT,\n" +
            "id VARCHAR(40) NOT NULL,\n" +
            "url VARCHAR(150) NOT NULL,\n" +
            "noteId VARCHAR(40) NOT NULL,\n" +
            "PRIMARY KEY ( `pid` )\n" +
            ")ENGINE=InnoDB DEFAULT CHARSET=utf8;";
    private final static Logger logger = LoggerFactory.getLogger(NoteController.class);


    @RequestMapping(value = "/", produces = "application/json", method = RequestMethod.GET)
    public HashMap<String,String> httpGet(@RequestHeader(value="Authorization") String comingM){
        statsDClient.incrementCounter("endpoint.get.timestamp.get");
        String[] userInfo = decodeBase64(comingM);
        String user_name = userInfo[0];
        String user_password = userInfo[1];
        User user = accountService.findAccountByName(user_name);

        HashMap<String,String> m = new HashMap<>();

        if(verify(user_password,user)){
            java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            m.put("Current Time", df.format(System.currentTimeMillis()));
        }else {
            logger.error("user authentication failed");
            m.put("Error", "You are not logged in!");
        }
        logger.info("timestamp returned");
        return m;

    }

    private boolean verify(String user_password, User user){
        boolean result = false;
        if(user != null && bCryptPasswordEncoder.matches(user_password, user.getUser_password()) )
        //if( bCryptPasswordEncoder.matches(user_password, "$2a$10$KwffF28hREFYPTtJ7FCguOzc2CBNSzWAICAm4XfDIsAQX0ZKWosSe") )
            result = true;
        return result;
    }

    protected static String[] decodeBase64(String comimgM){
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
    @RequestMapping(value = "/user/register", produces = "application/json", method = RequestMethod.POST)
    public HashMap<String,String> addUser(@RequestBody User comingM){
        statsDClient.incrementCounter("endpoint.create.user.post");
        accountService.createTables(sql1);
        accountService.createTables(sql2);
        accountService.createTables(sql3);
        accountService.createTables(sql4);
        String user_name = comingM.getUser_name();
        String user_password = comingM.getUser_password();


        HashMap<String,String> m = new HashMap<>();

        // 1:: Duplicate username done
        User user= accountService.findAccountByName(user_name);
        if(user!=null && user.getUser_name().equalsIgnoreCase(user_name)){
            logger.error("the user name already exists");
            m.put("Error", "Duplicate Value enter again!!");
            return m;
        }

        //2: : strong password code complying NIST
        final PasswordPolicy policy = new PasswordPolicy(BreachDatabase.haveIBeenPwned(5), 8, 64);

        //System.out.print("The message is:" +policy.check(user_password).toString());

        if(!policy.check(user_password).toString().equalsIgnoreCase("OK")){
            logger.error("The Password is not Strong.");
            m.put("Error", "The Password is not Strong.Please change it according to NIST!");
            return m;
        }

        // 3: check username for email in proper format
        if(isValid(user_name) && policy.check(user_password).toString().equalsIgnoreCase("OK")) {  //done
            User use = new User();

            use.setUser_name(user_name);
            use.setUser_password(bCryptPasswordEncoder.encode(user_password));

            accountService.add(use);
            logger.info("Perfect!! KO. You have been registered.");
            m.put("Success", "Perfect!! KO. You have been registered.");
            return m;
        }
        //4: check for the pattern of the Email address
        logger.error("Wrong Email Pattern. Enter Again!!");
        m.put("Error", "Wrong Email Pattern. Enter Again!!");
        return m;
    }
}
