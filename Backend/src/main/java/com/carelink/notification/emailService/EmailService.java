package com.carelink.notification.emailService;

import com.carelink.account.service.AccountService;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final String EMAIL_SUBJECT_PREFIX = "[CareLink+] ";

    private final AccountService accountService;

    public EmailService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void sendPasswordResetEmail(String email, String firstName, String resetLink) {
        String subject = EMAIL_SUBJECT_PREFIX + "Reset Your Password";
        String html = EmailTemplateBuilder.buildPasswordResetEmailHtml(firstName, resetLink);
        String text = EmailTemplateBuilder.buildPasswordResetEmailText(firstName, resetLink);
        accountService.sendEmail(email, subject, html, text);
    }

    public void sendWelcomeEmail(String email, String firstName, String username, String password) {
        String subject = EMAIL_SUBJECT_PREFIX + "Welcome – Your Access Details";
        String text = EmailTemplateBuilder.buildWelcomeEmailText(firstName, username, password);
        accountService.sendEmail(email, subject, text, text);
    }

    public void sendAdminResetEmail(String email, String firstName, String password) {
        String subject = EMAIL_SUBJECT_PREFIX + "Your Password Has Been Reset";
        String html = EmailTemplateBuilder.buildAdminResetEmailHtml(firstName, password);
        String text = EmailTemplateBuilder.buildAdminResetEmailText(firstName, password);
        accountService.sendEmail(email, subject, html, text);
    }

    public void sendAppointmentNotificationEmail(String email, String firstName, String doctorName, String date, String time) {
        String subject = EMAIL_SUBJECT_PREFIX + "New Appointment Scheduled";
        String html = EmailTemplateBuilder.buildAppointmentNotificationEmail(firstName, doctorName, date, time);
        accountService.sendEmail(email, subject, html, html);
    }

    public void sendAppointmentReminderEmail(String email, String firstName, String doctorName, String date, String time) {
        String subject = EMAIL_SUBJECT_PREFIX + "Appointment Reminder";
        String html = EmailTemplateBuilder.buildAppointmentReminderEmail(firstName, doctorName, date, time);
        accountService.sendEmail(email, subject, html, html);
    }

    public void sendAppointmentCancellationEmail(String email, String firstName, String doctorName, String date, String time) {
        String subject = EMAIL_SUBJECT_PREFIX + "Appointment Cancelled";
        String html = EmailTemplateBuilder.buildAppointmentCancellationEmail(firstName, doctorName, date, time);
        accountService.sendEmail(email, subject, html, html);
    }
}