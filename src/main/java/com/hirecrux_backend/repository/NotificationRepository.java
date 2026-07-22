package com.hirecrux_backend.repository;

import com.hirecrux_backend.entity.Notification;
import com.hirecrux_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUser(User user);
}
