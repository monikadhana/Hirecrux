package com.hirecrux_backend.dto.response;

import com.hirecrux_backend.enums.UserRole;
import lombok.Data;

@Data
public class UserResponseDto {
    private Integer userId;
    private String fullName;
    private String email;
    private String phone;
    private UserRole role;
    private Boolean isVerified;
}
