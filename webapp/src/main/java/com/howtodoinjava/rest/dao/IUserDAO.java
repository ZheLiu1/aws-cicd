package com.howtodoinjava.rest.dao;

import com.howtodoinjava.rest.model.User;

import java.util.List;

public interface IUserDAO {
    void createTables(String sql);

    int add(User account);

    int delete(int id);

    User findAccountById(int id);

    User findAccountByName(String name);
}