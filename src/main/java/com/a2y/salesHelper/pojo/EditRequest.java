package com.a2y.salesHelper.pojo;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Timestamp;
import java.time.OffsetDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EditRequest {
    private String participantName;
    private Long tenantId;
    private Long clientId;
    private OffsetDateTime createdAt;
    private String description;
}
