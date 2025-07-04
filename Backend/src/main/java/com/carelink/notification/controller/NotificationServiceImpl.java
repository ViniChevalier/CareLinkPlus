package com.carelink.notification.controller;

import com.carelink.grpc.notification.NotificationRequest;
import com.carelink.grpc.notification.NotificationResponse;
import com.carelink.grpc.notification.NotificationServiceGrpc;
import com.carelink.notification.model.Notification;
import com.carelink.notification.service.NotificationService;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationServiceImpl extends NotificationServiceGrpc.NotificationServiceImplBase {

    @Autowired
    private NotificationService notificationService;

    @Override
    public void sendNotification(NotificationRequest request, StreamObserver<NotificationResponse> responseObserver) {
        Notification notification = new Notification();
        notification.setUserId(Integer.parseInt(request.getRecipientId()));
        notification.setMessage(request.getMessage());
        notification.setNotificationType("GENERAL");
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.parse(request.getTimestamp()));

        notificationService.createNotification(notification);

        NotificationResponse response = NotificationResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Notification sent and saved successfully.")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}