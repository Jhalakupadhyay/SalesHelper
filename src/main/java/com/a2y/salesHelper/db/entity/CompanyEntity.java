package com.a2y.salesHelper.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "companies", schema = "sales")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompanyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "accounts", length = 500)
    private String accounts;

    @Column(name = "account_owner", length = 500)
    private String accountOwner;

    @Column(name = "type", length = 500)
    private String type;

    @Column(name = "focused_or_assigned", length = 500)
    private String focusedOrAssigned;

    @Column(name = "etm_region", length = 500)
    private String etmRegion;

    @Column(name = "account_tier", length = 500)
    private String accountTier;

    @Column(name = "meeting_update", length = 500)
    private String meetingUpdate;

    @Column(name = "quarter", length = 500)
    private String quarter;

    @Column(name = "meeting_initiative", length = 500)
    private String meetingInitiative;

    @Column(name = "sdr_responsible", length = 500)
    private String sdrResponsible;

    @Column(name = "sales_team_remarks", length = 500)
    private String salesTeamRemarks;

    @Column(name = "sdr_remark", length = 500)
    private String sdrRemark;

    @Column(name = "salespin_remark", length = 500)
    private String salespinRemark;

    @Column(name = "marketing_remark", length = 500)
    private String marketingRemark;

    @Column(name = "customer_name", length = 500)
    private String customerName;

    @Column(name = "designation", length = 500)
    private String designation;

    @Column(name = "mobile_number")
    private Long mobileNumber;

    @Column(name = "email", length = 500)
    private String email;
}
