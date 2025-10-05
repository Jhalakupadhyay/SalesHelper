package com.a2y.salesHelper.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a2y.salesHelper.db.entity.PersonaEntity;

@Repository
public interface PersonaRepository extends JpaRepository<PersonaEntity, Long> {

    /**
     * Find all company contacts by client ID
     */
    List<PersonaEntity> findByClientId(Long clientId);

    /**
     * Check if a company contact already exists with the same company, name,
     * designation, and client ID
     */
    boolean existsByCompanyAndNameAndDesignationAndClientId(String company, String name, String designation,
            Long clientId);

    /**
     * Find company contacts by company name (case-insensitive partial match) and
     * client ID
     */
    List<PersonaEntity> findByCompanyContainingIgnoreCaseAndClientId(String company, Long clientId);

    /**
     * Find company contacts by person name (case-insensitive partial match) and
     * client ID
     */
    List<PersonaEntity> findByNameContainingIgnoreCaseAndClientId(String name, Long clientId);

    /**
     * Find company contacts by designation (case-insensitive partial match) and
     * client ID
     */
    List<PersonaEntity> findByDesignationContainingIgnoreCaseAndClientId(String designation, Long clientId);

    /**
     * Find company contacts by company and client ID
     */
    List<PersonaEntity> findByCompanyAndClientId(String company, Long clientId);

    /**
     * Find company contacts by name and client ID
     */
    List<PersonaEntity> findByNameAndClientId(String name, Long clientId);

    /**
     * Get count of contacts by client ID
     */
    @Query("SELECT COUNT(c) FROM PersonaEntity c WHERE c.clientId = :clientId")
    Long countByClientId(@Param("clientId") Long clientId);

    /**
     * Get distinct companies by client ID
     */
    @Query("SELECT DISTINCT c.company FROM PersonaEntity c WHERE c.clientId = :clientId ORDER BY c.company")
    List<String> findDistinctCompaniesByClientId(@Param("clientId") Long clientId);

    /**
     * Get distinct designations by client ID
     */
    @Query("SELECT DISTINCT c.designation FROM PersonaEntity c WHERE c.clientId = :clientId AND c.designation IS NOT NULL ORDER BY c.designation")
    List<String> findDistinctDesignationsByClientId(@Param("clientId") Long clientId);

    // Tenant filtering methods
    List<PersonaEntity> findByTenantId(Long tenantId);

    @Query("SELECT c FROM PersonaEntity c WHERE c.clientId = :clientId AND c.tenantId = :tenantId")
    List<PersonaEntity> findByClientIdAndTenantId(@Param("clientId") Long clientId, @Param("tenantId") Long tenantId);

    boolean existsByCompanyAndNameAndDesignationAndClientIdAndTenantId(String company, String name, String designation,
            Long clientId, Long tenantId);

    @Query("SELECT COUNT(c) FROM PersonaEntity c WHERE c.clientId = :clientId AND c.tenantId = :tenantId")
    Long countByClientIdAndTenantId(@Param("clientId") Long clientId, @Param("tenantId") Long tenantId);

    @Query("SELECT DISTINCT c.company FROM PersonaEntity c WHERE c.clientId = :clientId AND c.tenantId = :tenantId ORDER BY c.company")
    List<String> findDistinctCompaniesByClientIdAndTenantId(@Param("clientId") Long clientId,
            @Param("tenantId") Long tenantId);
}