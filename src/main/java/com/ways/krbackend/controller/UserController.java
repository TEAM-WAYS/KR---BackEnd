package com.ways.krbackend.controller;

import com.ways.krbackend.DTO.ApiResponse;
import com.ways.krbackend.model.Manager;
import com.ways.krbackend.service.JwtTokenService;
import com.ways.krbackend.service.UserServiceImpl;
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
    UserServiceImpl userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenService jwtTokenService;

    @PostMapping("/new-user")
    public ResponseEntity<?> postUser(@RequestBody Manager manager){
        try {
            manager.setPwd(passwordEncoder.encode(manager.getPwd()));
            Optional<Manager> response = userService.postUser(manager);
            if (response.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("new user applied"));
            } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponse("connection error, try again"));
            }
        }catch (Exception error){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("system error: "+ error);
        }
    }
    @PostMapping("/login-user") public ResponseEntity<?> login(@RequestBody Manager manager) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(manager.getUserName(), manager.getPwd()));
         if(authentication.isAuthenticated()){
             String jwtToken = jwtTokenService.generateJwtToken(authentication);
               return ResponseEntity.status(HttpStatus.OK)
                       .header("Authorization", "Bearer " + jwtToken)
                       .body(new ApiResponse("Du er logget på"));
         } else {
         throw new UsernameNotFoundException("invalid user request..!!");
         }
    }
}