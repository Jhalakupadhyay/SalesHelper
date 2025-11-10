package com.a2y.salesHelper.controller;

import java.util.List;

import com.a2y.salesHelper.pojo.Participant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.a2y.salesHelper.config.CurrentUser;
import com.a2y.salesHelper.pojo.Notification;
import com.a2y.salesHelper.service.interfaces.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/notifications")
@Tag(name = "Notification Controller", description = "APIs related to Notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(summary = "Get Notifications for a User", description = "Fetches all notifications for the current user within their tenant")
    @GetMapping
    public ResponseEntity<List<Notification>> getNotificationsForUser() {
        Long userId = CurrentUser.getUserId();
        Long tenantId = CurrentUser.getTenantId();
        List<Notification> notifications = notificationService.getNotificationsForUserId(userId, tenantId);
        return ResponseEntity.ok(notifications);
    }

    @Operation(summary = "Mark Notification as Seen", description = "Marks a notification as seen by the current user within their tenant")
    @PostMapping
    public ResponseEntity<Boolean> addSeenByUserId(@RequestParam Long notificationId) {
        Long userId = CurrentUser.getUserId();
        Long tenantId = CurrentUser.getTenantId();
        Boolean result = notificationService.addSeenByUserId(userId, notificationId, tenantId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "API to return the participant entities for particular notification type")
    @GetMapping("/participantsByType")
    public ResponseEntity<List<Participant>> getParticipantsByNotificationType(@RequestParam String type) {
        Long tenantId = CurrentUser.getTenantId();
        Long userId = CurrentUser.getUserId();

        // Implementation to fetch participants by notification type
        List<Participant> notifications = notificationService.getNotificationsByType(type, userId, tenantId);
        return ResponseEntity.ok(notifications);
    }
}
