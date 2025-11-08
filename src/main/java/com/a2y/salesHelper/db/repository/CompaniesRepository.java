package com.a2y.salesHelper.db.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.a2y.salesHelper.db.entity.CompanyEntity;

public interface CompaniesRepository extends JpaRepository<CompanyEntity, Long> {

        /**
         * Delete a company by its ID.
         *
         * @param id the ID of the company to delete
         */
        void deleteById(Long id);

        @Query("SELECT c.accountName FROM CompanyEntity c WHERE c.clientId = :clientId")
        List<String> findAllAccounts(Long clientId);

        @Query("SELECT c FROM CompanyEntity c WHERE " +
                        "LOWER(c.accountName) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
                        "LOWER(c.aeNam) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
                        "LOWER(c.accountCategory) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
                        "LOWER(c.focusedOrAssigned) LIKE LOWER(CONCAT('%', :searchQuery, '%'))")
        List<CompanyEntity> searchByAccountOrAccountOwnerOrCustomerNameOrEmail(String searchQuery);

        @Query("SELECT c FROM CompanyEntity c WHERE c.clientId = :clientId AND LOWER(c.aeNam) LIKE LOWER(:aeName)")
        List<CompanyEntity> findByAeNamAndTenantIdAndClientIdIgnoreCase(String aeName, Long tenantId, Long clientId);

        @Query("SELECT c FROM CompanyEntity c WHERE c.clientId = :clientId AND LOWER(c.city) LIKE LOWER(:city) ")
        List<CompanyEntity> findByClientIdAndTenantIdAndCityIgnoreCase(String city, Long tenantId, Long clientId);

        @Query("SELECT c FROM CompanyEntity c WHERE c.clientId = :clientId AND c.focusedOrAssigned = :focusedOrAssigned")
        List<CompanyEntity> findByClientIdAndTenantIdAndFocusedOrAssigned(Long clientId, Long tenantId,
                        String focusedOrAssigned);

        @Query("SELECT c FROM CompanyEntity c WHERE c.tenantId = :tenantId AND c.clientId = :clientId")
        List<CompanyEntity> findAllByTenantIdAndClientId(Long tenantId, Long clientId);

        @Query("SELECT c FROM CompanyEntity c WHERE c.tenantId = :tenantId AND c.clientId = :clientId")
        Page<CompanyEntity> findAllByTenantIdAndClientId(Long tenantId, Long clientId, Pageable pageable);

        @Query("SELECT c.accountName FROM CompanyEntity c WHERE c.clientId = :clientId AND c.tenantId = :tenantId")
        List<String> findAllAccountsByTenantIdAndClientId(Long clientId, Long tenantId);

        @Query(value = "SELECT c.id FROM sales.companies c WHERE c.account ILIKE :organization AND c.client_id = :clientId AND c.tenant_id = :tenantId ORDER BY c.id DESC LIMIT 1", nativeQuery = true)
        List<Long> findByOrganizationAndClientIdAndTenantId(String organization, Long clientId, Long tenantId);

        Optional<CompanyEntity> findByIdAndClientIdAndTenantId(Long id, Long clientId, Long tenantId);

        @Query("SELECT c FROM CompanyEntity c WHERE c.clientId = :clientId AND c.accountName = :accountName AND c.tenantId = :tenantId")
        List<CompanyEntity> findByClientIdAndAccountNameAndTenantId(Long clientId, String accountName, Long tenantId);
}
