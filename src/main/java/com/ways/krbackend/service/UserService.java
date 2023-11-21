package com.ways.krbackend.service;

import com.ways.krbackend.model.User;

import java.util.Optional;

public interface UserService {
    Optional<User> postUser(User user);

    Optional<User> getUserWhereName(String userName);
}
