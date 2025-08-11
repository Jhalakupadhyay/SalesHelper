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
    @Query("SELECT c.id FROM CompanyEntity c WHERE c.accountName = :organization")
    Long findByAccountName(String organization);

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
