package com.a2y.salesHelper.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

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

    @Column(name = "org_name", length = 500)
    private String orgName;

    @Column(name = "cooldown1")
    private Long cooldownPeriod1;

    @Column(name = "cooldown2")
    private Long cooldownPeriod2;

    @Column(name = "cooldown3")
    private Long cooldownPeriod3;
}
