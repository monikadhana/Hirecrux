package com.hirecrux_backend.controller;

import com.hirecrux_backend.dto.request.LoginRequestDto;
import com.hirecrux_backend.dto.request.RegisterRequestDto;
import com.hirecrux_backend.dto.response.LoginResponseDto;
import com.hirecrux_backend.dto.response.UserResponseDto;
import com.hirecrux_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public UserResponseDto register(@RequestBody RegisterRequestDto request){
        return userService.register(request);
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto request){
        return userService.login(request);
    }

}
