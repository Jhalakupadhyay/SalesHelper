package com.a2y.salesHelper.db.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;

@Entity
@Table(name = "participants",schema = "sales")
@Data
@ToString
public class ParticipantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sheet_name")
    private String sheetName;

    @Column(name = "name", length = 500)
    private String name;

    @Column(name = "designation", length = 500)
    private String designation;

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
