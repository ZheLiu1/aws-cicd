package com.howtodoinjava.rest.Service;

import com.howtodoinjava.rest.model.Note;
import com.howtodoinjava.rest.model.User;

import java.util.List;

public interface IUserService {
    int add(User account);

    int update(User account);

    int delete(int id);

    User findAccountById(int id);

    User findAccountByName(String name);

    List<User> findAccountList();
}
