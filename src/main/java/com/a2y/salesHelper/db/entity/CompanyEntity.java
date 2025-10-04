package com.a2y.salesHelper.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "companies", schema = "dev")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompanyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "account", length = 500)
    private String accountName;

    @Column(name = "ae_name", length = 500)
    private String aeNam;

    @Column(name = "segment", length = 500)
    private String segment;

    @Column(name = "focused_or_assigned", length = 500)
    private String focusedOrAssigned;

    @Column(name = "account_status", length = 500)
    private String accountStatus;

    @Column(name = "pipeline_status", length = 500)
    private String pipelineStatus;

    @Column(name = "account_category", length = 500)
    private String accountCategory;

    @Column(name = "city")
    private String city;
}
