package com.americobarber.service;

import com.americobarber.dto.request.LoginRequest;
import com.americobarber.dto.request.RegisterRequest;
import com.americobarber.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    LoginResponse register(RegisterRequest request);
}
