package com.a2y.salesHelper.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cooldown {

    private long orgId;
    private OffsetDateTime cooldownPeriod1;
    private OffsetDateTime cooldownPeriod2;
    private OffsetDateTime cooldownPeriod3;
}
