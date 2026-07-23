package com.hirecrux_backend.service;

import com.hirecrux_backend.dto.request.CreateJobRequest;
import com.hirecrux_backend.dto.request.NotificationRequest;
import com.hirecrux_backend.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {
    NotificationResponse createNotification(NotificationRequest request);
    List<NotificationResponse> getMyNotifications();
    NotificationResponse markAsRead(Integer notificationId);
}
