package com.example.technicalTest.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.example.technicalTest.auth.security.JwtTokenProvider;
import com.example.technicalTest.user.dto.LoginRequest;
import com.example.technicalTest.user.dto.LoginResponse;
import com.example.technicalTest.user.entity.User;
import com.example.technicalTest.user.repository.UserRepository;
import com.example.technicalTest.user.utils.enums.Role;

class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() {
        LoginRequest req = new LoginRequest("user@mail.com", "1234");

        User u = new User();
        u.setEmail("user@mail.com");
        u.setRole(Role.ROLE_USER);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(userRepository.findByEmail("user@mail.com"))
                .thenReturn(Optional.of(u));

        when(jwtTokenProvider.generateToken("user@mail.com", Role.ROLE_USER))
                .thenReturn("FAKE_TOKEN");

        LoginResponse response = authService.login(req);

        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("FAKE_TOKEN");
        assertThat(response.email()).isEqualTo("user@mail.com");
        assertThat(response.role()).isEqualTo("ROLE_USER");

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("user@mail.com", "1234")
        );

        verify(jwtTokenProvider).generateToken("user@mail.com", Role.ROLE_USER);
    }

    @Test
    void login_ShouldThrow_WhenUserNotFound() {
        LoginRequest req = new LoginRequest("notfound@mail.com", "1234");

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(userRepository.findByEmail("notfound@mail.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuario no encontrado");
    }
}
