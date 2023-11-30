package com.ways.krbackend.service;

import com.ways.krbackend.model.email;

import java.util.List;
import java.util.Optional;

public interface EmailService {
    List<email> getEmails();
    void syncEmails();
    void deleteEmail(Long id);
    List<email> fetchEmailsFromRemote();
    email getEmailById(Long id);
    Optional<email> getContentById(Long id);

    void markEmailAsBanned(Long id);
}
