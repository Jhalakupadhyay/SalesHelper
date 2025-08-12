package com.a2y.salesHelper.db.repository;

import com.a2y.salesHelper.db.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

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

    //QUERY THAT WILL RETURN THE ID OF THE ROW WITH THE GIVEN ORGANIZATION
    /**
     * Find the ID of a company by its account name and client ID.
     * also first checks if the accountName starts with the first word of the organization
     * @param organization the account name of the company
     * @param clientId     the ID of the client
     * @return the ID of the company, or null if not found
     */
    @Query("SELECT c.id FROM CompanyEntity c WHERE c.accountName ILIKE :organization AND c.clientId = :clientId ORDER BY c.id DESC LIMIT 1")
    Long findByAccountName(String organization,Long clientId);

    /**
     * Find all companies for a specific client.
     *
     * @param clientId the ID of the client
     * @return a list of company entities
     */
    Optional<CompanyEntity> findByIdAndClientId(Long id,Long clientId);

    @Query("SELECT c FROM CompanyEntity c WHERE c.clientId = :clientId")
    List<CompanyEntity> findAllByClientId(Long clientId);

    @Query("SELECT c FROM CompanyEntity c WHERE c.clientId = :clientId AND c.accountName = :accountName")
    List<CompanyEntity> findByClientIdAndAccountName(Long clientId);
}
