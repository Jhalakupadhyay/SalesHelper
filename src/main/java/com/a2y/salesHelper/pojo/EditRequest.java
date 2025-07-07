package com.a2y.salesHelper.pojo;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Timestamp;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EditRequest {
    String participantName;
    Timestamp createdAt;
    String description;
}
