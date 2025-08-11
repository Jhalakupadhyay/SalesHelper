package com.a2y.salesHelper.db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

@Entity
@Table(name = "participants",schema = "sales")
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
