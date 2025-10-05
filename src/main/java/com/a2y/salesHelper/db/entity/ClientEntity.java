package com.a2y.salesHelper.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cooldown", schema = "sales")
public class ClientEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long orgId;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "org_name", length = 500)
    private String orgName;

    @Column(name = "cooldown1")
    private Long cooldownPeriod1;

    @Column(name = "cooldown2")
    private Long cooldownPeriod2;

    @Column(name = "cooldown3")
    private Long cooldownPeriod3;
}
