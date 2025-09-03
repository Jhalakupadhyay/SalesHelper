package com.a2y.salesHelper.db.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

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
