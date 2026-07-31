package com.hirecrux_backend.controller;

import com.hirecrux_backend.dto.request.LoginRequestDto;
import com.hirecrux_backend.dto.request.RegisterRequestDto;
import com.hirecrux_backend.dto.response.LoginResponseDto;
import com.hirecrux_backend.dto.response.UserResponseDto;
import com.hirecrux_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    @PostMapping("/register")
    public UserResponseDto register(@Valid @RequestBody RegisterRequestDto request){
        return userService.register(request);
    }

    @PostMapping("/login")
    public LoginResponseDto login(@Valid @RequestBody LoginRequestDto request){
        return userService.login(request);
    }

    @GetMapping("/profile")
    public String profile() {
        return "JWT Authentication Successful";
    }
}
