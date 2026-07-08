package com.hirecrux_backend.dto.response;

import com.hirecrux_backend.enums.UserRole;
import lombok.Data;

@Data
public class LoginResponseDto {
    private Integer userId;
    private String email;
    private String fullName;
    private UserRole role;
    private Boolean isVerified;
    private String token;
}
