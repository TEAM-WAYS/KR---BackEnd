package com.ways.krbackend.controller;

import com.ways.krbackend.model.User;
import com.ways.krbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/new-user")
    public ResponseEntity<?> postUser(@RequestBody User user){

        try {
            user.setPwd(passwordEncoder.encode(user.getPwd()));
            Optional<User> response = userService.postUser(user);
            if (response.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body("new user applied");
            } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("connection error, try again");
            }
        }catch (Exception error){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("system error: "+ error);
        }
    }
    @PostMapping("/user")
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

    @PostMapping("/login-user") public ResponseEntity<String> loginUser(@RequestBody User user) {
        System.out.println(user);
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPwd()));
         if(authentication.isAuthenticated()){
        //return JwtResponseDTO.builder()
        //        .accessToken(jwtService.GenerateToken(authRequestDTO.getUsername()).build();
               return ResponseEntity.status(HttpStatus.OK)
                       .body("Du er logget på");
         } else {
         throw new UsernameNotFoundException("invalid user request..!!");
         }
    }



}

