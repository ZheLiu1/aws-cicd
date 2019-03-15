package com.howtodoinjava.rest.Service;

import com.howtodoinjava.rest.dao.IUserDAO;
import com.howtodoinjava.rest.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements IUserService {
    @Autowired
    IUserDAO accountDAO;

    @Override
    public void createTables(String sql){
        accountDAO.createTables(sql);
    }
    @Override
    public int add(User account) {
        return accountDAO.add(account);
    }

    @Override
    public User findAccountByName(String name){
        return accountDAO.findAccountByName(name);
    }
}
