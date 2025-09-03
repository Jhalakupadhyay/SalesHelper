package com.a2y.salesHelper.service.interfaces;

import com.a2y.salesHelper.pojo.Notification;
import org.apache.xmlbeans.impl.xb.xmlconfig.Extensionconfig;

import java.util.List;

public interface NotificationService {

    //service that will remove the user from the notification list
    Boolean addSeenByUserId(Long userId, Long notificationId);

    //service to be polled by client for notifications for a user
    List<Notification> getNotificationsForUserId(Long userId);
}
