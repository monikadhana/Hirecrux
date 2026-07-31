package com.hirecrux_backend.dto.request;

import com.hirecrux_backend.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {
    @NotBlank @Email
    private String email;
    @NotBlank
    private String password;
}
