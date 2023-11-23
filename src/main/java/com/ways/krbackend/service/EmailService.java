package com.ways.krbackend.service;

import com.ways.krbackend.model.email;

import java.util.List;

public interface EmailService {
    List<email> getEmails();
    void syncEmails();
    void deleteEmail(Long id);
    List<email> fetchEmailsFromRemote();
    email getEmailById(Long id);
    String getContentById(Long id);
}
