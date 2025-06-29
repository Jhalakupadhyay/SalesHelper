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
public class Participant {

    private Long id;
    private String sheetName;
    private String name;
    private String designation;
    private String organization;
    private String email;
    private String mobile;
    private String attended;
    private String assignedUnassigned;
    private String eventName;
    private String eventDate;
    private String meetingDone;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private Boolean isFocused;
}
