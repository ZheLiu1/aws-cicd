package com.howtodoinjava.rest.dao;

import com.howtodoinjava.rest.model.User;

import java.util.List;

public interface IUserDAO {
    int add(User account);

    int update(User account);

    int delete(int id);

    User findAccountById(int id);

    User findAccountByName(String name);

    List<User> findAccountList();
}