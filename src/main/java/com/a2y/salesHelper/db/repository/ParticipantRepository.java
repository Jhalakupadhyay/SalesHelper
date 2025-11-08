package com.a2y.salesHelper.db.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.a2y.salesHelper.db.entity.ParticipantEntity;

public interface ParticipantRepository extends JpaRepository<ParticipantEntity, Long> {

        Boolean existsByNameAndDesignationAndOrganization(String name, String designation, String organization);

        @Query("SELECT p FROM ParticipantEntity p")
        List<ParticipantEntity> getAll();

        // Custom query to find participants by name, designation, or organization and
        // order by event date
        // IT CHECNKS FOR THE SAME STRIGN WETHER THE NAME DESIGNATION AND ORGANIZATION
        // STARTS WITH THE GIVEN STRING OR MATCHES IT
        @Query("SELECT p FROM ParticipantEntity p WHERE " +
                        "LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
                        "LOWER(p.designation) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
                        "LOWER(p.organization) LIKE LOWER(CONCAT('%', :name, '%')) " +
                        "AND p.clientId = :clientId " +
                        "ORDER BY p.eventDate")
        List<ParticipantEntity> findByNameOrDesignationOrOrganization(String name, Long clientId);

        Optional<ParticipantEntity> findFirstByNameAndDesignationAndOrganizationAndClientId(String participantName,
                        String designation, String organization, Long clientId);

        @Query("SELECT p FROM ParticipantEntity p WHERE " +
                        "LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND " +
                        "p.clientId = :clientId")
        List<ParticipantEntity> findByNameAndClientId(String name, Long clientId);

        @Query("SELECT p FROM ParticipantEntity p WHERE " +
                        "LOWER(p.designation) LIKE LOWER(CONCAT('%', :designation, '%')) AND " +
                        "p.clientId = :clientId")
        List<ParticipantEntity> findByDesignationAndClientId(String designation, Long clientId);

        @Query("SELECT p FROM ParticipantEntity p WHERE " +
                        "LOWER(p.organization) LIKE LOWER(CONCAT('%', :organization, '%')) AND " +
                        "p.clientId = :clientId")
        List<ParticipantEntity> findByOrganizationAndClientId(String organization, Long clientId);

        @Query("SELECT p FROM ParticipantEntity p where p.clientId = :clientId AND p.assignedUnassigned = :assignedUnassigned")
        List<ParticipantEntity> findByAssignedUnassignedAndClientId(String assignedUnassigned, Long clientId);

        @Query("SELECT p FROM ParticipantEntity p WHERE " +
                        "p.attended = :attended AND " +
                        "p.clientId = :clientId")
        List<ParticipantEntity> findByAttendedAndClientId(Boolean attended, Long clientId);

        @Query("SELECT p FROM ParticipantEntity p WHERE p.clientId = :clientId")
        List<ParticipantEntity> getAllByClientId(Long clientId);

        // get all the participants whos event date is between the start and end date
        @Query("SELECT p FROM ParticipantEntity p WHERE " +
                        "p.eventDate BETWEEN :startDate AND :endDate AND " +
                        "p.clientId = :clientId")
        List<ParticipantEntity> findByEventDateBetweenAndClientId(OffsetDateTime startDate, OffsetDateTime endDate,
                        Long clientId);

        List<ParticipantEntity> findByClientIdAndOrgId(Long orgId, Long clientId);

        List<ParticipantEntity> findByClientId(Long clientId);

        // Tenant filtering methods
        List<ParticipantEntity> findByTenantId(Long tenantId);

        @Query("SELECT p FROM ParticipantEntity p WHERE p.tenantId = :tenantId AND p.clientId = :clientId")
        List<ParticipantEntity> getAllByTenantIdAndClientId(Long tenantId, Long clientId);

        @Query("SELECT p FROM ParticipantEntity p WHERE p.tenantId = :tenantId AND p.clientId = :clientId")
        Page<ParticipantEntity> getAllByTenantIdAndClientId(Long tenantId, Long clientId, Pageable pageable);

        Optional<ParticipantEntity> findFirstByNameAndDesignationAndOrganizationAndClientIdAndTenantId(
                        String participantName, String designation, String organization, Long clientId, Long tenantId);

        @Query("SELECT p FROM ParticipantEntity p WHERE " +
                        "LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
                        "LOWER(p.designation) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
                        "LOWER(p.organization) LIKE LOWER(CONCAT('%', :name, '%')) " +
                        "AND p.clientId = :clientId AND p.tenantId = :tenantId " +
                        "ORDER BY p.eventDate")
        List<ParticipantEntity> findByNameOrDesignationOrOrganizationAndTenantId(String name, Long clientId,
                        Long tenantId);

        @Query("SELECT p FROM ParticipantEntity p WHERE " +
                        "p.eventDate BETWEEN :startDate AND :endDate AND " +
                        "p.clientId = :clientId AND p.tenantId = :tenantId")
        List<ParticipantEntity> findByEventDateBetweenAndClientIdAndTenantId(OffsetDateTime startDate,
                        OffsetDateTime endDate, Long clientId, Long tenantId);
}
