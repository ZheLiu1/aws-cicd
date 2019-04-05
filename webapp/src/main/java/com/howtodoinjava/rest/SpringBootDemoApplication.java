package com.howtodoinjava.rest;

import com.howtodoinjava.rest.Service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class SpringBootDemoApplication implements CommandLineRunner {
    @Autowired
    IUserService accountService;
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

    public static void main(String[] args){
        SpringApplication.run(SpringBootDemoApplication.class, args);
    }

    @Override
    public void run(String... args){
        accountService.createTables(sql1);
        accountService.createTables(sql2);
        accountService.createTables(sql3);
        accountService.createTables(sql4);
    }
}
