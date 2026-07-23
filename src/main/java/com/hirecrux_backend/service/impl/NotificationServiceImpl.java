package com.hirecrux_backend.service.impl;

import com.hirecrux_backend.dto.request.NotificationRequest;
import com.hirecrux_backend.dto.request.UpdateNotificationRequest;
import com.hirecrux_backend.dto.response.NotificationResponse;
import com.hirecrux_backend.entity.Notification;
import com.hirecrux_backend.entity.User;
import com.hirecrux_backend.repository.NotificationRepository;
import com.hirecrux_backend.repository.UserRepository;
import com.hirecrux_backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    public NotificationResponse createNotification(NotificationRequest request){
        Optional<User> userOptional = userRepository.findById(request.getUserId());
        if(userOptional.isEmpty()){
            throw new RuntimeException("User not found");
        }
        User user = userOptional.get();

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());

        notification.setIsRead(Boolean.FALSE);

        Notification savedNotification = notificationRepository.save(notification);

        NotificationResponse notificationResponse = new NotificationResponse();

        notificationResponse.setNotificationId(savedNotification.getNotificationId());
        notificationResponse.setUserId(user.getUserId());
        notificationResponse.setTitle(savedNotification.getTitle());
        notificationResponse.setMessage(savedNotification.getMessage());
        notificationResponse.setIsRead(savedNotification.getIsRead());
        notificationResponse.setCreatedAt(savedNotification.getCreatedAt());

        return notificationResponse;
    }

    @Override
    public List<NotificationResponse> getMyNotifications(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            throw new RuntimeException("User not found");
        }
        User user = userOptional.get();
        List<Notification> notifications = notificationRepository.findByUser(user);

        List<NotificationResponse> responses = new ArrayList<>();
        for(Notification notification:notifications){
            NotificationResponse notificationResponse = new NotificationResponse();

            notificationResponse.setNotificationId(notification.getNotificationId());
            notificationResponse.setTitle(notification.getTitle());
            notificationResponse.setMessage(notification.getMessage());
            notificationResponse.setIsRead(notification.getIsRead());
            notificationResponse.setCreatedAt(notification.getCreatedAt());

            responses.add(notificationResponse);
        }
        return responses;
    }

    public NotificationResponse markAsRead(Integer notificationId){
        Optional<Notification> notificationOptional = notificationRepository.findById(notificationId);
        if(notificationOptional.isEmpty()){
            throw new RuntimeException("No notifications");
        }
        Notification notification = notificationOptional.get();
        notification.setIsRead(Boolean.TRUE);
        Notification savedNotification = notificationRepository.save(notification);

        NotificationResponse notificationResponse = new NotificationResponse();
        notificationResponse.setIsRead(savedNotification.getIsRead());
        notificationResponse.setNotificationId(savedNotification.getNotificationId());
        notificationResponse.setTitle(savedNotification.getTitle());
        notificationResponse.setMessage(savedNotification.getMessage());
        notificationResponse.setCreatedAt(savedNotification.getCreatedAt());

        return notificationResponse;
    }
}
