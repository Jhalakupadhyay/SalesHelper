package com.a2y.salesHelper.db.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "interaction_history", schema = "dev")
@IdClass(CompositeIdClass.class)
public class InteractionHistoryEntity {

    @Id
    @Column(name = "participant_name")
    private String participantName;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "organization", length = 500)
    private String organization;

    @Column(name = "designation", length = 500)
    private String designation;

    @Column(name = "event_name", length = 500)
    private String eventName;

    @Column(name = "event_date")
    private OffsetDateTime eventDate;

    @Column(name = "cooldown_date", nullable = false)
    OffsetDateTime cooldownDate;

    @Column(name = "cooldown_count", nullable = false)
    private Integer cooldownCount;

    @Column(name = "description")
    private String description;

    @Column(name = "meeting_done")
    private Boolean meetingDone;

    @Id
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}
