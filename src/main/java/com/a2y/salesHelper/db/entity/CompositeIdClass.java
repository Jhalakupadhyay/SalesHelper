package com.a2y.salesHelper.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompositeIdClass implements Serializable {
    private Long participantId;
    private String eventName;
}
