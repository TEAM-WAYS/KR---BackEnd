package com.ways.krbackend.controller;

import com.ways.krbackend.DTO.ApplicationPoints;
import com.ways.krbackend.model.Application;
import com.ways.krbackend.model.email;
import com.ways.krbackend.service.ChatGtpApiService;
import com.ways.krbackend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private ChatGtpApiService chatGtpApiService;

    @GetMapping("/emails")
    public List<email> getEmails() {
        return emailService.getEmails();
    }

    @GetMapping("/emails/sync")
    public void syncEmail() {
        emailService.syncEmails();
    }

    @DeleteMapping("/emails/delete/{id}")
    public void deleteEmail(@PathVariable Long id) {
        emailService.deleteEmail(id);
    }

    @GetMapping("/emails/{id}")
    public ResponseEntity<email> getEmailById(@PathVariable Long id) {
        email email = emailService.getEmailById(id);

        if (email != null) {
            return new ResponseEntity<>(email, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/emails/content/{id}")
    public ResponseEntity<Object> getContentById(@PathVariable Long id) {
        Optional<email> content = emailService.getContentById(id);

        if (content != null) {
            Map<String, Optional<email>> response = new HashMap<>();
            response.put("content", content);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/emails/transform") // TODO: metode skal tage alle nye email
    public ResponseEntity<Object> turnEmailIntoApplication(){
        System.out.println("--endpoint email/transform running--");
        ResponseEntity<Object> response = chatGtpApiService.turnEmailIntoApplication();
        if(response.equals(HttpStatus.OK) ){
            System.out.println("--endpoint email/transform success--");
            return new ResponseEntity<>(HttpStatus.OK);
        }else {
            return  new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }

    }


}
