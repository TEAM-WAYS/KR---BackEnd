package com.ways.krbackend.service;

import com.ways.krbackend.model.User;
import com.ways.krbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserServiceImpl implements UserService{
    @Autowired
    UserRepository userRepository;

    @Override
    public Optional<User> postUser(User user) {
        return Optional.of(userRepository.save(user));
    }
}
