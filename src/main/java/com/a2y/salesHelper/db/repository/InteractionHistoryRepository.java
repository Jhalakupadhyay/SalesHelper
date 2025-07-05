package com.a2y.salesHelper.db.repository;

import com.a2y.salesHelper.db.entity.InteractionHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface InteractionHistoryRepository extends JpaRepository<InteractionHistoryEntity, Long> {

    /**
     * Find InteractionHistory by participantId
     * @param participantId ID of the participant
     * @return InteractionHistory object if found, otherwise null
     */
    InteractionHistoryEntity findByParticipantIdAndEventDate(Long participantId, OffsetDateTime eventDate);

    /**
     * Find InteractionHistory by participantId
     * @param participantId ID of the participant
     * @return InteractionHistory object if found, otherwise null
     */
    List<InteractionHistoryEntity> findByParticipantId(Long participantId);
}
