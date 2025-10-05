package com.a2y.salesHelper.db.entity;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "participants", schema = "sales")
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ParticipantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "sheet_name")
    private String sheetName;

    @Column(name = "name", length = 500)
    private String name;

    @Column(name = "designation", length = 500)
    private String designation;

    @Column(name = "city", length = 500)
    private String city;

    @Column(name = "organization", length = 500)
    private String organization;

    @Column(name = "email", length = 500)
    private String email;

    @Column(name = "mobile", length = 100)
    private String mobile;

    @Column(name = "attended", length = 100)
    private String attended;

    @Column(name = "assigned_unassigned", length = 100)
    private String assignedUnassigned;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "event_date")
    private OffsetDateTime eventDate;

    @Column(name = "event_name", length = 500)
    private String eventName;

    @Column(name = "org_id")
    private Long orgId;

    @Column(name = "is_good_lead")
    private Boolean isGoodLead;

    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
}
