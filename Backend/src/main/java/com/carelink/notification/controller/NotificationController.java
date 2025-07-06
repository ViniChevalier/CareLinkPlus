package com.carelink.notification.controller;

import com.carelink.notification.dto.NotificationRequestDTO;
import com.carelink.notification.model.Notification;
import com.carelink.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public Notification createNotification(@RequestBody NotificationRequestDTO dto) {
        Notification notification = new Notification();
        notification.setUserId(dto.getUserId());
        notification.setNotificationType(dto.getNotificationType());
        notification.setMessage(dto.getMessage());
        notification.setIsRead(false);

        return notificationService.createNotification(notification, dto.getUserEmail(), dto.getUserPhone());
    }

    @GetMapping("/{id}")
    public Notification getNotification(@PathVariable Integer id) {
        return notificationService.getNotificationById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
    }

    @GetMapping("/user/{userId}")
    public List<Notification> getNotificationsByUser(@PathVariable Integer userId,
                                                     @RequestParam(required = false) Boolean isRead) {
        return notificationService.getNotificationsByUser(userId, isRead);
    }

    @PutMapping("/{id}/read")
    public Notification markAsRead(@PathVariable Integer id) {
        return notificationService.markAsRead(id);
    }

    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable Integer id) {
        notificationService.deleteNotification(id);
    }
}