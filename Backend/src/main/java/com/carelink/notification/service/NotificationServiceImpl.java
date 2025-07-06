package com.carelink.notification.service;

import com.carelink.notification.model.Notification;
import com.carelink.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private JavaMailSender mailSender;

    private final String TWILIO_SID = "SEU_TWILIO_SID";
    private final String TWILIO_AUTH_TOKEN = "SEU_TWILIO_AUTH_TOKEN";
    private final String TWILIO_NUMBER = "+SEU_NUM_TWILIO";

    @Override
    public Notification createNotification(Notification notification, String userEmail, String userPhone) {
        Notification saved = notificationRepository.save(notification);

        if (userEmail != null && !userEmail.isEmpty()) {
            sendEmail(userEmail, "Nova notificação", notification.getMessage());
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
        throw new RuntimeException("Notification not found");
    }

    @Override
    public void deleteNotification(Integer id) {
        notificationRepository.deleteById(id);
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    private void sendSMS(String to, String body) {
        Twilio.init(TWILIO_SID, TWILIO_AUTH_TOKEN);
        Message.creator(new PhoneNumber(to), new PhoneNumber(TWILIO_NUMBER), body).create();
    }
}