package com.example.technicalTest.user.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.technicalTest.user.dto.UserRegisterRequest;
import com.example.technicalTest.user.service.UserService;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_ShouldReturnCreatedAndCallService() {

        UserRegisterRequest request = mock(UserRegisterRequest.class);

        ResponseEntity<Void> responseEntity = userController.register(request);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isNull();

        verify(userService).register(request);
        verifyNoMoreInteractions(userService);
    }
}
