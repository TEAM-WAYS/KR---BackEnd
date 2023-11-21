package com.ways.krbackend.conroller;

import com.ways.krbackend.model.User;
import com.ways.krbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/user")
    public ResponseEntity<?> postUser(@RequestBody User user){
        Optional<User> respons = userService.postUser(user);
        try {
        user.setPwd(passwordEncoder.encode(user.getPwd()));

            if (respons.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body("new user applied");
            } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("connection error, try again");
            }
        }catch (Exception error){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("system error: "+ error);
        }
    }
}

