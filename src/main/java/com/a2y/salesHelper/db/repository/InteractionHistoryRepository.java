package com.a2y.salesHelper.db.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.a2y.salesHelper.db.entity.InteractionHistoryEntity;

public interface InteractionHistoryRepository extends JpaRepository<InteractionHistoryEntity, Long> {

        List<InteractionHistoryEntity> findByTenantIdAndParticipantNameAndOrganizationAndClientId(Long tenantId,
                        String participantName,
                        String organization, Long clientId);

        List<InteractionHistoryEntity> findAllByCooldownDateIsBefore(OffsetDateTime cooldownDate);

        List<InteractionHistoryEntity> findAllByCooldownDateIsAfter(OffsetDateTime offsetDateTime);

        Optional<InteractionHistoryEntity> findByParticipantNameAndCreatedAtAndClientIdAndTenantId(
                        String participantName,
                        OffsetDateTime createdAt, Long clientId, Long tenantId);

        Optional<InteractionHistoryEntity> findTopByParticipantNameAndOrganizationAndClientIdAndTenantIdOrderByCreatedAtDesc(
                        String participantName, String organization, Long clientId, Long tenantId);

}
