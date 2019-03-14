package com.howtodoinjava.rest.Service;

import com.howtodoinjava.rest.model.User;

import java.util.List;

public interface IUserService {
    void createTables(String sql);

    int add(User account);

    User findAccountByName(String name);
}
