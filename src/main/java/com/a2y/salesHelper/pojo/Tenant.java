package com.a2y.salesHelper.pojo;

import java.time.LocalDateTime;

import com.a2y.salesHelper.enums.SubscriptionPlan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {

    private Long tenantId;
    private String tenantName;
    private SubscriptionPlan subscriptionPlan;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
