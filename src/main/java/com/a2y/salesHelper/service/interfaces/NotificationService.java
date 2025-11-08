package com.a2y.salesHelper.service.interfaces;

import java.util.List;

import com.a2y.salesHelper.pojo.Notification;

public interface NotificationService {

    // service that will remove the user from the notification list
    Boolean addSeenByUserId(Long userId, Long notificationId, Long tenantId);

    // service to be polled by client for notifications for a user
    List<Notification> getNotificationsForUserId(Long userId, Long tenantId);
}
