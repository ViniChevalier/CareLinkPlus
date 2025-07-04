package com.carelink.notification.service;

import com.carelink.notification.model.Notification;
import com.carelink.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public List<Notification> getUserNotifications(Integer userId) {
        return notificationRepository.findByUserId(userId);
    }

    public Optional<Notification> markAsRead(Integer notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setIsRead(true);
            notificationRepository.save(notification);
            return Optional.of(notification);
        }
        return Optional.empty();
    }
}