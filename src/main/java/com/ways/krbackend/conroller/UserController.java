package com.ways.krbackend.conroller;

import com.ways.krbackend.model.User;
import com.ways.krbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
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
        Optional<User> response = userService.postUser(user);
        try {
        user.setPwd(passwordEncoder.encode(user.getPwd()));

            if (response.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body("new user applied");
            } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("connection error, try again");
            }
        }catch (Exception error){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("system error: "+ error);
        }
    }
    @GetMapping("/user")
    public ResponseEntity<?> getUser(@RequestBody User userR){
        String hashedPwdR = passwordEncoder.encode(userR.getPwd());
        ResponseEntity response = null;
        try {
            Optional<User> optional = userService.getUserWhereName(userR.getUserName());
            if (optional.isPresent()) {
                String hashedPwdF = optional.get().getPwd();
                if(hashedPwdF.equals(hashedPwdR)){
                    response = ResponseEntity.status(HttpStatus.ACCEPTED).body("");
                }else {
                    response = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("wrong username or password");
                }
            } else {
                response = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("wrong username or password");
            }
        }catch (Exception error){
            response =ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error: "+ error);
        }
        return response;
    }
}

