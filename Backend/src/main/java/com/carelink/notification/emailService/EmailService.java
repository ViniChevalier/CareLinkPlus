package com.carelink.notification.emailService;

import org.springframework.stereotype.Service;
import com.carelink.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class EmailService {

    private static final String EMAIL_SUBJECT_PREFIX = "[CareLink+] ";
  
    @Autowired
    private NotificationService notificationService;

    public void sendPasswordResetEmail(String email, String firstName, String resetLink, Integer userId) {
        String subject = EMAIL_SUBJECT_PREFIX + "Reset Your Password";
        String html = EmailTemplateBuilder.buildPasswordResetEmailHtml(firstName, resetLink);
        String text = EmailTemplateBuilder.buildPasswordResetEmailText(firstName, resetLink);
        notificationService.sendEmail(email, subject, html, text, userId);
    }

    public void sendWelcomeEmail(String email, String firstName, String username, String password, Integer userId) {
        String subject = EMAIL_SUBJECT_PREFIX + "Welcome – Your Access Details";
        String text = EmailTemplateBuilder.buildWelcomeEmailText(firstName, username, password);
        notificationService.sendEmail(email, subject, text, text, userId);
    }

    public void sendAdminResetEmail(String email, String firstName, String password, Integer userId) {
        String subject = EMAIL_SUBJECT_PREFIX + "Your Password Has Been Reset";
        String html = EmailTemplateBuilder.buildAdminResetEmailHtml(firstName, password);
        String text = EmailTemplateBuilder.buildAdminResetEmailText(firstName, password);
        notificationService.sendEmail(email, subject, html, text, userId);
    }

    public void sendAppointmentNotificationEmail(String email, String firstName, String doctorName, String date, String time, Integer userId) {
        String subject = EMAIL_SUBJECT_PREFIX + "New Appointment Scheduled";
        String html = EmailTemplateBuilder.buildAppointmentNotificationEmail(firstName, doctorName, date, time);
        notificationService.sendEmail(email, subject, html, html, userId);
    }

    public void sendAppointmentReminderEmail(String email, String firstName, String doctorName, String date, String time, Integer userId) {
        String subject = EMAIL_SUBJECT_PREFIX + "Appointment Reminder";
        String html = EmailTemplateBuilder.buildAppointmentReminderEmail(firstName, doctorName, date, time);
        notificationService.sendEmail(email, subject, html, html, userId);
    }

    public void sendAppointmentCancellationEmail(String email, String firstName, String doctorName, String date, String time, Integer userId) {
        String subject = EMAIL_SUBJECT_PREFIX + "Appointment Cancelled";
        String html = EmailTemplateBuilder.buildAppointmentCancellationEmail(firstName, doctorName, date, time);
        notificationService.sendEmail(email, subject, html, html, userId);
    }
}