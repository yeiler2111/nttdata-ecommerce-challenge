package com.example.technicalTest.user.dto;

public record LoginResponse(
        String token,
        String email,
        String role
) {}
