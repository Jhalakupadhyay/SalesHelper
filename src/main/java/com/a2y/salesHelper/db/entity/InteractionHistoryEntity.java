package com.a2y.salesHelper.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "interaction_history", schema = "sales")
@IdClass(CompositeIdClass.class)
public class InteractionHistoryEntity {

    @Id
    @Column(name = "participant_name")
    private String participantName;

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

    @Column(name = "cooldown_count",nullable = false)
    private Integer cooldownCount;

    @Column(name = "description")
    private String description;

    @Column(name = "meeting_done")
    private Boolean meetingDone;

    @Id
    @Column(name = "created_at",nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}
