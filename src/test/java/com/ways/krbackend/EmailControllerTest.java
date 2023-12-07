package com.ways.krbackend;

import com.ways.krbackend.DTO.ApplicationPoints;
import com.ways.krbackend.controller.EmailController;
import com.ways.krbackend.model.Email;
import com.ways.krbackend.service.ChatGtpApiService;
import com.ways.krbackend.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmailControllerTest {

    @Mock
    private ChatGtpApiService chatGtpApiService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailController emailController;

    @Test
    public void testGetEmails() {
        // Arange
        List<Email> mockEmails = Arrays.asList(
                new Email(),
                new Email()
        );
        when(emailService.getEmails()).thenReturn(mockEmails);

        // Act
        List<Email> result = emailController.getEmails();

        // Asert
        assertEquals(mockEmails, result);
    }


    @Test
    public void testSyncEmail() {
        // Arange

        // Act
        emailController.syncEmail();

        // Assert
        verify(emailService).syncEmails();
    }

    @Test
    public void testDeleteEmail() {
        // rrange
        Long emailIdToDelete = 1L;

        // Acot
        emailController.deleteEmail(emailIdToDelete);

        // Assert
        verify(emailService).deleteEmail(emailIdToDelete);
    }

    @Test
    public void testGetEmailById() {
        // Arrange
        Long emailId = 1L;
        Email mockEmail = new Email();
        when(emailService.getEmailById(emailId)).thenReturn(mockEmail);

        // Actt
        ResponseEntity<Email> responseEntity = emailController.getEmailById(emailId);

        // Asse
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockEmail, responseEntity.getBody());
    }

    @Test
    public void testGetContentById() {
        // Arange
        Long emailId = 1L;
        Optional<Email> mockContent = Optional.of(new Email());
        when(emailService.getContentById(emailId)).thenReturn(mockContent);

        // Act
        ResponseEntity<Object> responseEntity = emailController.getContentById(emailId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockContent, ((Map<String, Optional<Email>>) responseEntity.getBody()).get("content"));
    }


    @Test
    void searchByInquiry_ValidInquiry_ReturnsApplicationPointsList() {
        // Arrange
        String inquiry = "validInquiry";
        List<ApplicationPoints> expectedPointsList = new LinkedList<>();
        Mockito.when(chatGtpApiService.validateApplicationsQuick(inquiry))
                .thenReturn(Optional.<LinkedList<ApplicationPoints>>of((LinkedList<ApplicationPoints>) expectedPointsList));

        // Act
        List<ApplicationPoints> result = emailController.searchByInquiry(inquiry);

        // Assert
        assertEquals(expectedPointsList, result);
    }

}



