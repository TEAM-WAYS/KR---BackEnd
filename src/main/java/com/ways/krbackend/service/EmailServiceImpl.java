package com.ways.krbackend.service;

import com.ways.krbackend.model.email;
import com.ways.krbackend.repository.emailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.*;
import java.util.*;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;


@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private emailRepository emailRepository;

    @Value("${spring.mail.username}")
    private String emailUsername;

    @Value("${spring.mail.password}")
    private String emailPassword;

    private static final String PROTOCOL = "imaps";
    private static final String HOST = "imap.gmail.com";
    private static final String INBOX_FOLDER = "INBOX";
    private static final String TRASH_FOLDER = "[Gmail]/Trash";

    @Override
    public List<email> getEmails() {
        return emailRepository.findAll();
    }
    @Scheduled(fixedRate = 1800000)
    public void autoSyncEmails() {
        syncEmails();
    }
    @Override
    public void syncEmails() {
        try {
            List<email> remoteEmails = fetchEmailsFromRemote();

            for (email remoteEmail : remoteEmails) {
                Optional<email> existingEmail = emailRepository.findBySubject(remoteEmail.getSubject());

                if (existingEmail.isPresent()) {
                    //update or skip
                    email localEmail = existingEmail.get();
                    localEmail.setFromAddress(remoteEmail.getFromAddress());
                    localEmail.setSentDate(remoteEmail.getSentDate());
                    emailRepository.save(localEmail);
                } else {
                    emailRepository.save(remoteEmail);
                    //new mail
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<email> fetchEmailsFromRemote() {
        List<email> emails = new ArrayList<>();

        try {
            Properties properties = new Properties();
            properties.put("mail.store.protocol", PROTOCOL);
            properties.put("mail.imaps.ssl.enable", "true");

            Session session = Session.getInstance(properties);

            Store store = session.getStore(PROTOCOL);
            store.connect(HOST, emailUsername, emailPassword);

            Folder inbox = store.getFolder(INBOX_FOLDER);
            inbox.open(Folder.READ_ONLY);

            try {
                Message[] messages = inbox.getMessages();

                for (Message message : messages) {
                    email email = new email();
                    email.setSubject(message.getSubject());
                    email.setFromAddress(message.getFrom()[0].toString());
                    email.setSentDate(message.getSentDate());
                    email.setContent(getEmailContent(message));
                    //message.setFlag(Flags.Flag.SEEN, true);
                    emails.add(email);
                }
            } finally {
                inbox.close(false);
                store.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return emails;
    }

    private String getEmailContent(Message message) throws Exception {
        Object content = message.getContent();

        if (content instanceof Multipart) {
            Multipart multipart = (Multipart) content;
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                sb.append(bodyPart.getContent());
            }

            return sb.toString();
        } else if (content instanceof String) {
            return (String) content;
        } else {
            return "Unsupported content type";
        }
    }

    @Override
    public void deleteEmail(Long id) {
        try {
            Properties properties = new Properties();
            properties.put("mail.store.protocol", PROTOCOL);
            properties.put("mail.imaps.ssl.enable", "true");

            Session session = Session.getInstance(properties);

            Store store = session.getStore(PROTOCOL);
            store.connect(HOST, emailUsername, emailPassword);

            Folder inbox = store.getFolder(INBOX_FOLDER);
            inbox.open(Folder.READ_WRITE);

            try {
                SearchTerm searchTerm = new SubjectTerm("Your Email Subject"); // Modify this as needed
                Message[] messages = inbox.search(searchTerm);

                for (Message message : messages) {
                    if (message.getHeader("Message-ID")[0].equals(id.toString())) {
                        Folder trashFolder = store.getFolder(TRASH_FOLDER);
                        inbox.copyMessages(new Message[]{message}, trashFolder);
                        message.setFlag(Flags.Flag.DELETED, true);
                        break;
                    }
                }
            } finally {
                inbox.close(true);
                store.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public email getEmailById(Long id) {
        Optional<email> optionalEmail = emailRepository.findById(id);
        return optionalEmail.orElse(null);
    }

    @Override
    public Optional<email> getContentById(Long id) {
        return emailRepository.findById(id);
    }
}