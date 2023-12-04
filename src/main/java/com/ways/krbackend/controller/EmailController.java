package com.ways.krbackend.controller;

import com.ways.krbackend.DTO.ApplicationPoints;
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

    @PostMapping("/emails/forbid")
    public ResponseEntity<String> markEmailAsForbidden(@RequestBody Map<String, Long> payload) {
        try {
            Long emailId = payload.get("emailId");
            emailService.markEmailAsForbidden(emailId);
            return new ResponseEntity<>("Email marked as forbidden.", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to mark email as forbidden.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

    @GetMapping("/email/search")
    public List<ApplicationPoints> searchByInquiry( @RequestParam String inquiry){
        Optional<LinkedList<ApplicationPoints>> applicationPointsList = chatGtpApiService.validateApplicationsQuick(inquiry);
        if(applicationPointsList!=null){
            return applicationPointsList.get();
        }
        return null;

    }
}
