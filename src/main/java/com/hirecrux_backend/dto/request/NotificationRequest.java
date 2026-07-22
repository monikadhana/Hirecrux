package com.hirecrux_backend.dto.request;

import com.hirecrux_backend.entity.User;
import lombok.Data;

@Data
public class NotificationRequest {
    private User user;
    private String title;
    private String message;
}
