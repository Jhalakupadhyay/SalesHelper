package com.a2y.salesHelper.pojo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class Notification {

    List<Long> participantIds;
    Long notificationId;
    String type;
}
