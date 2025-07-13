package com.a2y.salesHelper.pojo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientPojo {

    private Long orgId;
    private String orgName;
    private Long cooldownPeriod1;
    private Long cooldownPeriod2;
    private Long cooldownPeriod3;
}
