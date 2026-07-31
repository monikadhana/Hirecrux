package com.hirecrux_backend.controller;

import com.hirecrux_backend.dto.request.NotificationRequest;
import com.hirecrux_backend.dto.response.NotificationResponse;
import com.hirecrux_backend.entity.Notification;
import com.hirecrux_backend.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor

@RequestMapping("/api/notification")
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/create-notification")
    public NotificationResponse createNotification(@Valid @RequestBody NotificationRequest request){
        return notificationService.createNotification(request);
    }

    @GetMapping("/myNotification")
    public List<NotificationResponse> getMyNotifications(){
        return notificationService.getMyNotifications();
    }

    @PutMapping("/markAsRead/{notificationId}")
    public NotificationResponse markAsRead(@PathVariable Integer notificationId){
        return notificationService.markAsRead(notificationId);
    }
}
