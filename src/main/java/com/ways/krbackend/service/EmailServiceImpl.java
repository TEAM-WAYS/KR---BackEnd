package com.ways.krbackend.service;

import com.ways.krbackend.model.Email;
import com.ways.krbackend.repository.emailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
    private static final long MAX_ATTACHMENT_SIZE_BYTES = 10 * 1024 * 1024; //bytes

    @Override
    public List<Email> getEmails() {
        return emailRepository.findAll();
    }
    public String removeNonHtml(String htmlString) {
        System.out.println("non processed: " + htmlString);
        int htmlIndex = htmlString.indexOf("<html>");

        String cleanHtml = (htmlIndex != -1) ? htmlString.substring(htmlIndex) : htmlString;
        System.out.println(cleanHtml);
        return cleanHtml;
    }
    public Optional<Email> htmlEmail(Email email) {
            String cleanContent = removeNonHtml(email.getContent());
            email.setContent(cleanContent);

        return Optional.of(email);
    }

    @Scheduled(fixedRate = 2000000)
    public void autoSyncEmails() {
        syncEmails();
    }
    @Override
    public boolean syncEmails() {
        try {
            List<Email> remoteEmails = fetchEmailsFromRemote();

            for (Email remoteEmail : remoteEmails) {
                Optional<Email> existingEmail = emailRepository.findBySubject(remoteEmail.getSubject());

                if (existingEmail.isPresent()) {
                    Email localEmail = existingEmail.get();
                    localEmail.setFromAddress(remoteEmail.getFromAddress());
                    localEmail.setSentDate(remoteEmail.getSentDate());
                    emailRepository.save(localEmail);
                } else {
                    emailRepository.save(remoteEmail);
                }
            } return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private byte[] getAttachmentsFromMessage(Message message) throws Exception {
        Multipart multipart = (Multipart) message.getContent();
        List<byte[]> attachmentDataList = new ArrayList<>();

        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                InputStream attachmentStream = bodyPart.getInputStream();
                long attachmentSize = getAttachmentSize(attachmentStream);
                if (attachmentSize > MAX_ATTACHMENT_SIZE_BYTES) {
                    byte[] skippedAttachmentData = createSkippedAttachmentData(bodyPart.getFileName());
                    attachmentDataList.add(skippedAttachmentData);
                    continue;
                }
                byte[] attachmentData = setAttachmentData(attachmentStream);
                attachmentDataList.add(attachmentData);

                System.out.println("Saved attachment: " + bodyPart.getFileName());
            }
        }
        return combineAttachmentData(attachmentDataList);
    }

    private byte[] setAttachmentData(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        System.out.println("Read attachment data successfully.");

        return outputStream.toByteArray();
    }

    private byte[] combineAttachmentData(List<byte[]> attachmentDataList) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            for (byte[] attachmentData : attachmentDataList) {
                outputStream.write(attachmentData);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    private long getAttachmentSize(InputStream inputStream) throws IOException {
        long size = 0;
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            size += bytesRead;
        }
        return size;
    }

    private byte[] createSkippedAttachmentData(String fileName) {
        String message = "Attachment Skipped: " + fileName;
        return message.getBytes(StandardCharsets.UTF_8);
    }
    @Override
    public List<Email> fetchEmailsFromRemote() {
        List<Email> emails = new ArrayList<>();

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
                    Email email = new Email();
                    email.setSubject(message.getSubject());
                    email.setFromAddress(message.getFrom()[0].toString());
                    email.setSentDate(message.getSentDate());
                    email.setContent(getEmailContent(message));
                    email.setAttachments(getAttachmentsFromMessage(message));

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

        if (content instanceof String) {
            return (String) content;
        } else if (content instanceof Multipart) {
            Multipart multipart = (Multipart) content;
            StringBuilder textContent = new StringBuilder();

            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain") || bodyPart.isMimeType("text/html")) {
                    textContent.append(bodyPart.getContent().toString());
                } else if (bodyPart.isMimeType("multipart/*")) {
                    textContent.append(gmailIsStupid((Multipart) bodyPart.getContent()));
                } else if (bodyPart.getDisposition() != null
                        && bodyPart.getDisposition().equalsIgnoreCase(Part.ATTACHMENT)) {
                }
            }

            return textContent.toString();
        } else {
            return content.toString();
        }
    }

    private String gmailIsStupid(Multipart nestedMultipart) throws Exception {
        StringBuilder nestedTextContent = new StringBuilder();

        for (int i = 0; i < nestedMultipart.getCount(); i++) {
            BodyPart nestedBodyPart = nestedMultipart.getBodyPart(i);
            if (nestedBodyPart.isMimeType("text/plain") || nestedBodyPart.isMimeType("text/html")) {
                nestedTextContent.append(nestedBodyPart.getContent().toString());
            } else if (nestedBodyPart.isMimeType("multipart/*")) {
                nestedTextContent.append(gmailIsStupid((Multipart) nestedBodyPart.getContent()));
            } else if (nestedBodyPart.getDisposition() != null
                    && nestedBodyPart.getDisposition().equalsIgnoreCase(Part.ATTACHMENT)) {
            }
        }

        return nestedTextContent.toString();
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
    public Email getEmailById(Long id) {
        Optional<Email> optionalEmail = emailRepository.findById(id);
        return optionalEmail.orElse(null);
    }

    @Override
    public Optional<Email> getContentById(Long id) {
        return emailRepository.findById(id);
    }
}