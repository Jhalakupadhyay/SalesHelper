package com.a2y.salesHelper.db.repository;

import com.a2y.salesHelper.db.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CompaniesRepository extends JpaRepository<CompanyEntity, Long> {


    /**
     * Find a company by its accounts.
     *
     * @param accounts the accounts of the company
     * @return the company entity if found, otherwise null
     */
    Boolean existsByAccounts(String accounts);

    /**
     * Delete a company by its ID.
     *
     * @param id the ID of the company to delete
     */
    void deleteById(Long id);

    @Query("SELECT c.accounts FROM CompanyEntity c WHERE c.clientId = :clientId")
    List<String> findAllAccounts(Long clientId);

    @Query("SELECT c FROM CompanyEntity c WHERE " +
            "LOWER(c.accounts) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
            "LOWER(c.accountOwner) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
            "LOWER(c.customerName) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchQuery, '%'))")
    List<CompanyEntity> searchByAccountOrAccountOwnerOrCustomerNameOrEmail(String searchQuery);

    //QUERY THAT WILL RETURN THE ID OF THE ROW WITH THE GIVEN ORGANIZATION
    @Query("SELECT c.id FROM CompanyEntity c WHERE c.accounts ILIKE :organization")
    Long findByAccounts(String organization);

    /**
     * Find all companies for a specific client.
     *
     * @param clientId the ID of the client
     * @return a list of company entities
     */
    Optional<CompanyEntity> findByIdAndClientId(Long id,Long clientId);

    @Query("SELECT c FROM CompanyEntity c WHERE c.clientId = :clientId")
    List<CompanyEntity> findAllByClientId(Long clientId);
}
