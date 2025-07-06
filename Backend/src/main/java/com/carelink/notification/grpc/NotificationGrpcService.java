package com.carelink.notification.grpc;

import com.carelink.notification.model.Notification;
import com.carelink.notification.service.NotificationService;
import com.carelink.grpc.notification.NotificationRequest;
import com.carelink.grpc.notification.NotificationResponse;
import com.carelink.grpc.notification.NotificationIdRequest;
import com.carelink.grpc.notification.UserNotificationsRequest;
import com.carelink.grpc.notification.NotificationListResponse;
import com.carelink.grpc.notification.NotificationServiceGrpc;
import com.carelink.grpc.notification.Empty;

import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NotificationGrpcService extends NotificationServiceGrpc.NotificationServiceImplBase {

    private final NotificationService notificationService;

    public NotificationGrpcService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    private NotificationResponse toGrpcResponse(Notification notification) {
        return NotificationResponse.newBuilder()
                .setNotificationId(notification.getNotificationId())
                .setUserId(notification.getUserId())
                .setNotificationType(notification.getNotificationType() != null ? notification.getNotificationType() : "")
                .setMessage(notification.getMessage())
                .setIsRead(notification.getIsRead())
                .setCreatedAt(notification.getCreatedAt() != null ? notification.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "")
                .build();
    }

    @Override
    public void createNotification(NotificationRequest request, StreamObserver<NotificationResponse> responseObserver) {
        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setNotificationType(request.getNotificationType());
        notification.setMessage(request.getMessage());
        notification.setIsRead(false);

        Notification saved = notificationService.createNotification(notification, request.getUserEmail(), request.getUserPhone());
        responseObserver.onNext(toGrpcResponse(saved));
        responseObserver.onCompleted();
    }

    @Override
    public void getNotification(NotificationIdRequest request, StreamObserver<NotificationResponse> responseObserver) {
        Notification notification = notificationService.getNotificationById(request.getNotificationId())
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        responseObserver.onNext(toGrpcResponse(notification));
        responseObserver.onCompleted();
    }

    @Override
    public void getUserNotifications(UserNotificationsRequest request, StreamObserver<NotificationListResponse> responseObserver) {
        Boolean isRead = request.getIsRead();
        List<Notification> notifications = notificationService.getNotificationsByUser(request.getUserId(), isRead);

        NotificationListResponse.Builder builder = NotificationListResponse.newBuilder();
        for (Notification notif : notifications) {
            builder.addNotifications(toGrpcResponse(notif));
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void markAsRead(NotificationIdRequest request, StreamObserver<NotificationResponse> responseObserver) {
        Notification updated = notificationService.markAsRead(request.getNotificationId());
        responseObserver.onNext(toGrpcResponse(updated));
        responseObserver.onCompleted();
    }

    @Override
    public void deleteNotification(NotificationIdRequest request, StreamObserver<Empty> responseObserver) {
        notificationService.deleteNotification(request.getNotificationId());
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }
}