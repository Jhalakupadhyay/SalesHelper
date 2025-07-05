package com.a2y.salesHelper.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InteractionHistory {
    private Long participantId;
    private String eventName;
    private OffsetDateTime eventDate;
    private String description;
}
