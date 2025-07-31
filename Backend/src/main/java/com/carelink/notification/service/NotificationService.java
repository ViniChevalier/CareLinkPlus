package com.carelink.notification.service;

import com.carelink.notification.model.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationService {
    Notification createNotification(Notification notification, String userEmail, String userPhone);

    List<Notification> getNotificationsByUser(Integer userId, Boolean isRead);

    Optional<Notification> getNotificationById(Integer id);

    Notification markAsRead(Integer id);
    
    void deleteNotification(Integer id);

    void sendEmail(String to, String subject, String htmlBody, String plainTextBody, Integer userId);
}