package com.a2y.salesHelper.pojo;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Companies {
    private Long id;
    private Long clientId;
    private String accountName;
    private String aeNam;
    private String segment;
    private String focusedOrAssigned;
    private String accountStatus;
    private String pipelineStatus;
    private String accountCategory;
}
