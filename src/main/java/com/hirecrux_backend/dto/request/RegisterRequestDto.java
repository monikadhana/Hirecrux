package com.hirecrux_backend.dto.request;

import lombok.Data;

@Data
public class RegisterRequestDto {
    private String fullName;
    private String email;
    private String password;
    private String phone;
}
