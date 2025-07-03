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
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Cooldown {

    @Id
    private long orgId;
    private OffsetDateTime cooldownPeriod1;
    private OffsetDateTime cooldownPeriod2;
    private OffsetDateTime cooldownPeriod3;
}
