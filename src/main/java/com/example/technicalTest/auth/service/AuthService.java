package com.example.technicalTest.auth.service;

import com.example.technicalTest.user.dto.LoginRequest;
import com.example.technicalTest.user.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
