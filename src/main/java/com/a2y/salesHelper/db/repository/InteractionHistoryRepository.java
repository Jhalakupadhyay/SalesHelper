package com.a2y.salesHelper.db.repository;

import com.a2y.salesHelper.db.entity.InteractionHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.security.Timestamp;
import java.sql.Date;
import java.time.OffsetDateTime;
import java.util.List;

public interface InteractionHistoryRepository extends JpaRepository<InteractionHistoryEntity, Long> {

    InteractionHistoryEntity findByParticipantNameAndEventNameAndOrganization(String participantName, String eventName, String organization);

    List<InteractionHistoryEntity> findByParticipantNameAndOrganizationAndClientId(String participantName, String organization, Long clientId);

    InteractionHistoryEntity findByParticipantNameAndCreatedAtAndClientId(String participantName, OffsetDateTime createdAt, Long clientId);

    InteractionHistoryEntity findTopByParticipantNameAndOrganizationAndClientIdOrderByCreatedAtDesc(String participantName, String organization , Long clientId);

    List<InteractionHistoryEntity> findAllByCooldownDateIsBefore(OffsetDateTime cooldownDate);

    List<InteractionHistoryEntity> findAllByCooldownDateIsAfter(OffsetDateTime offsetDateTime);
}
