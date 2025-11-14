package com.example.technicalTest.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.technicalTest.user.dto.UserRegisterRequest;
import com.example.technicalTest.user.entity.User;
import com.example.technicalTest.user.repository.UserRepository;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_ShouldSaveUser_WhenEmailNotExists() {
        UserRegisterRequest req = new UserRegisterRequest("Yeiler", "test@mail.com", "1234");

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("1234")).thenReturn("ENCODED");

        userService.register(req);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldThrow_WhenEmailExists() {
        UserRegisterRequest req = new UserRegisterRequest("Yeiler", "test@mail.com", "1234");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.register(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email ya está registrado");
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenExists() {
        User u = new User();
        u.setEmail("test@mail.com");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(u));

        User result = userService.findByEmail("test@mail.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@mail.com");
    }

    @Test
    void findByEmail_ShouldThrow_WhenNotFound() {
        when(userRepository.findByEmail("no@mail.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByEmail("no@mail.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuario no encontrado");
    }
}
