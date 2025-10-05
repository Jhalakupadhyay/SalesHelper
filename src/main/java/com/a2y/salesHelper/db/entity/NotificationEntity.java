package com.a2y.salesHelper.db.entity;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notification", schema = "sales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    // Option 1: Using @ElementCollection (Recommended)
    // This creates a separate join table for each collection
    @ElementCollection
    @CollectionTable(name = "entity_participant_ids",
            schema = "sales",
            joinColumns = @JoinColumn(name = "entity_id"))
    @Column(name = "participant_id")
    private List<Long> participantIds;

    @ElementCollection
    @CollectionTable(name = "entity_user_ids",
            schema = "sales",
            joinColumns = @JoinColumn(name = "entity_id"))
    @Column(name = "user_id")
    private List<Long> userIds;

    @Column(name = "type")
    private String type;
}
