package com.example.technicalTest.auth.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.technicalTest.auth.service.AuthService;
import com.example.technicalTest.user.dto.LoginRequest;
import com.example.technicalTest.user.dto.LoginResponse;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_ShouldReturnOkWithBody_WhenServiceReturnsResponse() {
        
        LoginRequest request = new LoginRequest("user@mail.com", "1234");
        LoginResponse loginResponse = mock(LoginResponse.class);

        when(authService.login(request)).thenReturn(loginResponse);

        
        ResponseEntity<LoginResponse> responseEntity = authController.login(request);

        
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isSameAs(loginResponse);

        verify(authService).login(request);
        verifyNoMoreInteractions(authService);
    }
}
