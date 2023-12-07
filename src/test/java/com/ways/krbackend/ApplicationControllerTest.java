package com.ways.krbackend;

import com.ways.krbackend.controller.ApplicationController;
import com.ways.krbackend.model.Application;
import com.ways.krbackend.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ApplicationControllerTest {

    @Mock
    private ApplicationService applicationService;

    @InjectMocks
    private ApplicationController applicationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testPostApplicationSuccess() {
        // Arrang
        Application application = new Application(); // create a sample application
        when(applicationService.postApplication(application)).thenReturn(Optional.of(application));

        // Act
        ResponseEntity<?> responseEntity = applicationController.postApplication(application);

        // Asert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("saved", responseEntity.getBody());
    }

    @Test
    void testPostApplicationFailure() {
        // Arrange
        Application application = new Application(); // create a sample application
        when(applicationService.postApplication(application)).thenReturn(Optional.empty());

         //  Act
        ResponseEntity<?> responseEntity = applicationController.postApplication(application);

        // Asert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("not saved", responseEntity.getBody());
    }
}
