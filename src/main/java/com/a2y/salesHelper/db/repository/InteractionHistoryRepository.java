package com.a2y.salesHelper.db.repository;

import com.a2y.salesHelper.db.entity.InteractionHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.security.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;

public interface InteractionHistoryRepository extends JpaRepository<InteractionHistoryEntity, Long> {

    InteractionHistoryEntity findByParticipantNameAndEventNameAndOrganization(String participantName, String eventName, String organization);

    List<InteractionHistoryEntity> findByParticipantNameAndOrganization(String participantName, String organization);

    InteractionHistoryEntity findByParticipantNameAndCreatedAt(String participantName, OffsetDateTime createdAt);
}
