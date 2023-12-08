package com.ways.krbackend;

import com.ways.krbackend.DTO.ApiResponse;
import com.ways.krbackend.controller.UserController;
import com.ways.krbackend.model.Manager;
import com.ways.krbackend.service.JwtTokenService;
import com.ways.krbackend.service.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenService jwtTokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPostUserSuccess() {
        Manager mockManager = new Manager();
        mockManager.setUserName("Andr921eas"); //angivet Test brugernavbn
        mockManager.setPwd("83ee"); // du skal angive PWD for testen yo

        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userService.postUser(any())).thenReturn(Optional.of(mockManager));

        ResponseEntity<?> responseEntity = userController.postUser(mockManager);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("new user applied", ((ApiResponse) responseEntity.getBody()).getMessage());
    }

    @Test
    public void testPostUserNoContent() {
        Manager mockManager = new Manager();
        mockManager.setUserName("Andr921eas");
        mockManager.setPwd("83ee");

        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userService.postUser(any())).thenReturn(Optional.empty());

        ResponseEntity<?> responseEntity = userController.postUser(mockManager);

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertEquals("connection error, try again", ((ApiResponse) responseEntity.getBody()).getMessage());
    }

    @Test
    public void testPostUserInternalServerError() {
        Manager mockManager = new Manager();
        mockManager.setUserName("Andr921eas");
        mockManager.setPwd("83ee");

        when(passwordEncoder.encode(any())).thenThrow(new RuntimeException("Some error"));

        ResponseEntity<?> responseEntity = userController.postUser(mockManager);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("system error: java.lang.RuntimeException: Some error", responseEntity.getBody());
    }


    @Test
    void testLoginSuccess() {
        Manager manager = new Manager();
        manager.setUserName("Andr921eas");
        manager.setPwd("83ee");

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(jwtTokenService.generateJwtToken(authentication)).thenReturn("testToken");

        ResponseEntity<ApiResponse> responseEntity = (ResponseEntity<ApiResponse>) userController.login(manager);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Bearer testToken", responseEntity.getHeaders().getFirst("Authorization"));
        assertEquals("Du er logget på", responseEntity.getBody().getMessage());

        verify(authenticationManager).authenticate(any());
        verify(jwtTokenService).generateJwtToken(authentication);
    }



    @Test
    void testLoginFailure() {
        Manager manager = new Manager();
        manager.setUserName("testUser");
        manager.setPwd("testPassword");

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        try {
            userController.login(manager);
        } catch (Exception e) {
            assertEquals(UsernameNotFoundException.class, e.getClass());
            assertEquals("invalid user request..!!", e.getMessage());
        }

        verify(authenticationManager).authenticate(any());
        verify(jwtTokenService, never()).generateJwtToken(any());
    }
}
