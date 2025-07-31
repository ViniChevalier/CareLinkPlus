package com.carelink.notification.service;

import com.carelink.exception.BusinessLogicException;
import com.carelink.exception.ResourceNotFoundException;

import com.carelink.notification.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import com.carelink.notification.repository.NotificationRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import com.carelink.account.repository.UserRepository;
import com.carelink.account.model.User;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    private final String TWILIO_SID = "TWILIO_SID";
    private final String TWILIO_AUTH_TOKEN = "TWILIO_AUTH_TOKEN";
    private final String TWILIO_NUMBER = "+TWILIO_NUMBER";

    @Override
    public Notification createNotification(Notification notification, String userEmail, String userPhone) {
        Notification saved = notificationRepository.save(notification);

        if (userEmail != null && !userEmail.isEmpty()) {
            Integer userId = getUserIdByEmail(userEmail);
            sendEmail(userEmail, "New Notification", notification.getMessage(), notification.getMessage(), userId);
        }

        if (userPhone != null && !userPhone.isEmpty()) {
            sendSMS(userPhone, notification.getMessage());
        }

        return saved;
    }

    @Override
    public List<Notification> getNotificationsByUser(Integer userId, Boolean isRead) {
        if (isRead == null) {
            return notificationRepository.findByUserId(userId);
        }
        return notificationRepository.findByUserIdAndIsRead(userId, isRead);
    }

    @Override
    public Optional<Notification> getNotificationById(Integer id) {
        return notificationRepository.findById(id);
    }

    @Override
    public Notification markAsRead(Integer id) {
        Optional<Notification> optional = notificationRepository.findById(id);
        if (optional.isPresent()) {
            Notification notif = optional.get();
            notif.setIsRead(true);
            return notificationRepository.save(notif);
        }
        throw new ResourceNotFoundException("Notification not found with ID: " + id);
    }

    @Override
    public void deleteNotification(Integer id) {
        notificationRepository.deleteById(id);
    }

    @Override
    public void sendEmail(String to, String subject, String htmlBody, String plainTextBody, Integer userId) {
        try {
            jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(
                    message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(plainTextBody, htmlBody);

            mailSender.send(message);

            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setNotificationType("EMAIL");
            notification.setMessage(subject);
            notification.setIsRead(false);

            System.out.println("Preparing to save notification:");
            System.out.println("  Email: " + to);
            System.out.println("  User ID: " + notification.getUserId());
            System.out.println("  Subject: " + subject);

            if (notification.getUserId() != null) {
                notificationRepository.save(notification);
            } else {
                System.out.println("⚠️ Cannot save notification: userId is null");
            }
        } catch (Exception e) {
            throw new BusinessLogicException("Failed to send email");
        }
    }

    private void sendSMS(String to, String body) {
        Twilio.init(TWILIO_SID, TWILIO_AUTH_TOKEN);
        Message.creator(new PhoneNumber(to), new PhoneNumber(TWILIO_NUMBER), body).create();
    }

    private Integer getUserIdByEmail(String email) {
        System.out.println("Looking up user ID for email: " + email);
        User user = userRepository.findByEmail(email);
        return user != null ? user.getUserID() : null;
    }
}
