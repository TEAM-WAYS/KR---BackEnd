package com.ways.krbackend;

import com.ways.krbackend.controller.EmailController;
import com.ways.krbackend.model.email;
import com.ways.krbackend.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmailControllerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailController emailController;

    @Test
    public void testGetEmails() {
        // Arange
        List<email> mockEmails = Arrays.asList(
                new email(),
                new email()
        );
        when(emailService.getEmails()).thenReturn(mockEmails);

        // Act
        List<email> result = emailController.getEmails();

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
        email mockEmail = new email();
        when(emailService.getEmailById(emailId)).thenReturn(mockEmail);

        // Actt
        ResponseEntity<email> responseEntity = emailController.getEmailById(emailId);

        // Asse
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockEmail, responseEntity.getBody());
    }

    @Test
    public void testGetContentById() {
        // Arange
        Long emailId = 1L;
        Optional<email> mockContent = Optional.of(new email());
        when(emailService.getContentById(emailId)).thenReturn(mockContent);

        // Act
        ResponseEntity<Object> responseEntity = emailController.getContentById(emailId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockContent, ((Map<String, Optional<email>>) responseEntity.getBody()).get("content"));
    }

}


