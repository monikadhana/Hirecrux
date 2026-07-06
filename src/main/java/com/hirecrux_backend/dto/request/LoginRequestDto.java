package com.hirecrux_backend.dto.request;

import com.hirecrux_backend.enums.UserRole;
import lombok.Data;

@Data
public class LoginRequestDto {
    private String email;
    private String password;
}
