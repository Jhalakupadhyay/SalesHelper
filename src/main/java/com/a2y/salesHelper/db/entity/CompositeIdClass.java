package com.a2y.salesHelper.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompositeIdClass implements Serializable {
    private String participantName;
    private Timestamp createdAt;
}
