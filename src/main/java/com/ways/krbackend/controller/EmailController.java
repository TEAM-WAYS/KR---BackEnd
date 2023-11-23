package com.ways.krbackend.controller;

import com.ways.krbackend.model.email;
import com.ways.krbackend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

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
            // Wrap the content in a JSON object
            Map<String, Optional<email>> response = new HashMap<>();
            response.put("content", content);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
