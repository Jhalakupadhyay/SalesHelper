package com.a2y.salesHelper.controller;

import com.a2y.salesHelper.pojo.Notification;
import com.a2y.salesHelper.service.interfaces.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@Tag(name = "Notification Controller", description = "APIs related to Notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(
            summary = "Get Notifications for a User",
            description = "Fetches all notifications for a given user ID"
    )
    @GetMapping
    public ResponseEntity<List<Notification>> getNotificationsForUser(Long userId) {
        List<Notification> notifications = notificationService.getNotificationsForUserId(userId);
        return ResponseEntity.ok(notifications);
    }


    @Operation(
            summary = "Mark Notification as Seen",
            description = "Marks a notification as seen by a specific user"
    )
    @PostMapping
    public ResponseEntity<Boolean> addSeenByUserId(Long userId, Long notificationId) {
        Boolean result = notificationService.addSeenByUserId(userId, notificationId);
        return ResponseEntity.ok(result);
    }
}
