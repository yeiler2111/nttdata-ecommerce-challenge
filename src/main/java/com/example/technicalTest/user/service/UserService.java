package com.example.technicalTest.user.service;

import com.example.technicalTest.user.dto.UserRegisterRequest;
import com.example.technicalTest.user.entity.User;

public interface UserService {

    void register(UserRegisterRequest request);

    User findByEmail(String email);
}