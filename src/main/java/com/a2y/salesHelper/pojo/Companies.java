package com.a2y.salesHelper.pojo;

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
    private String accounts;
    private String accountOwner;
    private String type;
    private String focusedOrAssigned;
    private String etmRegion;
    private String accountTier;
    private String meetingUpdate;
    private String quarter;
    private String meetingInitiative;
    private String sdrResponsible;
    private String salesTeamRemarks;
    private String sdrRemark;
    private String salespinRemark;
    private String marketingRemark;
    private String customerName;
    private String designation;
    private Long mobileNumber;
    private String email;
    private OffsetDateTime coolDownPeriod;
    private int coolDownCount;
}
