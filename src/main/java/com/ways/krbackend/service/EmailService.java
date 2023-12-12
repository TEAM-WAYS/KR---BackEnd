package com.ways.krbackend.service;

import com.ways.krbackend.model.Email;

import java.util.List;
import java.util.Optional;

public interface EmailService {
    List<Email> getEmails();
    public Optional<Email> htmlEmail(Email email);
    void syncEmails();
    void deleteEmail(Long id);
    List<Email> fetchEmailsFromRemote();
    Email getEmailById(Long id);
    Optional<Email> getContentById(Long id);
}
