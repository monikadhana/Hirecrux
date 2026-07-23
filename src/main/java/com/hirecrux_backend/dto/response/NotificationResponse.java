package com.hirecrux_backend.dto.response;

import com.hirecrux_backend.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponse {
    private Integer notificationId;
    private Integer userId;
    private String title;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
