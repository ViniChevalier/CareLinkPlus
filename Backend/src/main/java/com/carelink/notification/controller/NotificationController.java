package com.carelink.notification.controller;

import com.carelink.notification.dto.NotificationRequestDTO;
import com.carelink.notification.model.Notification;
import com.carelink.notification.service.NotificationService;
import com.carelink.exception.ResourceNotFoundException;
import com.carelink.exception.BusinessLogicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    public Notification createNotification(@RequestBody NotificationRequestDTO dto) {
        Notification notification = new Notification();
        notification.setUserId(dto.getUserId());
        notification.setNotificationType(dto.getNotificationType());
        notification.setMessage(dto.getMessage());
        notification.setIsRead(false);

        try {
            return notificationService.createNotification(notification, dto.getUserEmail(), dto.getUserPhone());
        } catch (Exception e) {
            throw new BusinessLogicException("Failed to create notification.");
        }
    }

    @GetMapping("/{id}")   
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM') or hasRole('PATIENT') or hasRole('RECEPTIONIST')")
    public Notification getNotification(@PathVariable Integer id) {
        return notificationService.getNotificationById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID: " + id));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM') or hasRole('PATIENT') or hasRole('RECEPTIONIST')")
    public List<Notification> getNotificationsByUser(@PathVariable Integer userId,
                                                     @RequestParam(required = false) Boolean isRead) {
        return notificationService.getNotificationsByUser(userId, isRead);
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    public Notification markAsRead(@PathVariable Integer id) {
        try {
            return notificationService.markAsRead(id);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Notification not found with ID: " + id);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteNotification(@PathVariable Integer id) {
        try {
            notificationService.deleteNotification(id);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Notification not found with ID: " + id);
        }
    }
}