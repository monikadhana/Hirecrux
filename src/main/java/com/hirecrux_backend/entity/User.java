package com.hirecrux_backend.entity;

import jakarta.persistence.*;
import com.hirecrux_backend.enums.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private  String email;

    @Column(name = "password",nullable = false)
    private String password;

    @Column(name = "phone",length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

}
