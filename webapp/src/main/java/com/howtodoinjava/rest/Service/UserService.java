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
    public int add(User account) {
        return accountDAO.add(account);
    }

    @Override
    public int update(User account) {
        return accountDAO.update(account);
    }

    @Override
    public int delete(int id) {
        return accountDAO.delete(id);
    }

    @Override
    public User findAccountById(int id) {
        return accountDAO.findAccountById(id);
    }

    @Override
    public User findAccountByName(String name){
        return accountDAO.findAccountByName(name);
    }

    @Override
    public List<User> findAccountList() {
        return accountDAO.findAccountList();
    }
}
