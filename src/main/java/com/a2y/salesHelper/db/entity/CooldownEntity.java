package com.a2y.salesHelper.db.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cooldown", schema = "sales")
public class CooldownEntity {
    @Id
    private long orgId;
    private OffsetDateTime cooldownPeriod1;
    private OffsetDateTime cooldownPeriod2;
    private OffsetDateTime cooldownPeriod3;
}
