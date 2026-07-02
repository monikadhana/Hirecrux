package com.hirecrux_backend.service.impl;

import com.hirecrux_backend.repository.NotificationRepository;
import com.hirecrux_backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
}
