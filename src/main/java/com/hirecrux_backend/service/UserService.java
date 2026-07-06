package com.hirecrux_backend.service;

import com.hirecrux_backend.dto.request.LoginRequestDto;
import com.hirecrux_backend.dto.request.RegisterRequestDto;
import com.hirecrux_backend.dto.response.LoginResponseDto;
import com.hirecrux_backend.dto.response.UserResponseDto;

public interface UserService {
    UserResponseDto register(RegisterRequestDto request);
    LoginResponseDto login(LoginRequestDto request);

}
