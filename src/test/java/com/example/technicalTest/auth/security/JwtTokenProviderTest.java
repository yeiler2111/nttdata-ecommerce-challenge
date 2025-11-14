package com.example.technicalTest.auth.security;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.technicalTest.user.utils.enums.Role;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    private final String SECRET = "12345678901234567890123456789012";
    private final long EXPIRATION_MS = 2000;

    @BeforeEach
    void setup() {
        jwtTokenProvider = new JwtTokenProvider(SECRET, EXPIRATION_MS);
    }

    @Test
    void testGenerateToken_shouldCreateValidJwt() {
        String token = jwtTokenProvider.generateToken("test@mail.com", Role.ROLE_USER);

        assertThat(token).isNotNull();
        assertThat(token.split("\\.").length).isEqualTo(3);
    }

    @Test
    void testGetEmailFromToken_shouldExtractCorrectEmail() {
        String token = jwtTokenProvider.generateToken("test@mail.com", Role.ROLE_ADMIN);

        String email = jwtTokenProvider.getEmailFromToken(token);

        assertThat(email).isEqualTo("test@mail.com");
    }

    @Test
    void testGetRoleFromToken_shouldExtractRole() {
        String token = jwtTokenProvider.generateToken("admin@mail.com", Role.ROLE_ADMIN);

        String role = jwtTokenProvider.getRoleFromToken(token);

        assertThat(role).isEqualTo(Role.ROLE_ADMIN.name());
    }

    @Test
    void testValidateToken_shouldReturnTrueForValidToken() {
        String token = jwtTokenProvider.generateToken("user@mail.com", Role.ROLE_USER);

        boolean valid = jwtTokenProvider.validateToken(token);

        assertThat(valid).isTrue();
    }

    @Test
    void testValidateToken_shouldReturnFalseForInvalidSignature() {
        String token = jwtTokenProvider.generateToken("user@mail.com", Role.ROLE_USER);

        token = token.substring(0, token.length() - 1) + "X";

        boolean valid = jwtTokenProvider.validateToken(token);

        assertThat(valid).isFalse();
    }

    @Test
    void testValidateToken_shouldReturnFalseForExpiredToken() throws Exception {
        JwtTokenProvider fastExpirationProvider = new JwtTokenProvider(SECRET, 100);

        String token = fastExpirationProvider.generateToken("user@mail.com", Role.ROLE_USER);

        TimeUnit.MILLISECONDS.sleep(150);

        boolean valid = fastExpirationProvider.validateToken(token);

        assertThat(valid).isFalse();
    }
}
