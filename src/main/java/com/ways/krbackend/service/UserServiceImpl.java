package com.ways.krbackend.service;

import com.ways.krbackend.model.User;
import com.ways.krbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("loadUser kaldt: user=" + username);
        String userName, password = null;
        List<GrantedAuthority> authorities = null;
        Optional<User> user = null;
        try {
            user = userRepository.findByUserName(username);
        } catch (Exception ex) {
            System.out.println("Database fejl =" + ex.getMessage());
        }
        if (user.isPresent()) {
            userName = user.get().getUserName();
            password = user.get().getPwd();
            authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(user.get().getRole()));
        } else {
            throw new UsernameNotFoundException("User details not found for the user:" + username);
        }
        return new org.springframework.security.core.userdetails.User(username,password,authorities);
    }

    public Optional<User> postUser(User user) {
        return Optional.of(userRepository.save(user));
    }
}
