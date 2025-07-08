package com.a2y.salesHelper.db.repository;


import com.a2y.salesHelper.db.entity.ParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<ParticipantEntity, Long> {

    Boolean existsByNameAndDesignationAndOrganization(String name, String designation,String organization);

    @Query("SELECT p FROM ParticipantEntity p")
    List<ParticipantEntity> getAll();

    // Custom query to find participants by name, designation, or organization and order by event date
    //IT CHECNKS FOR THE SAME STRIGN WETHER THE NAME DESIGNATION AND ORGANIZATION STARTS WITH THE GIVEN STRING OR MATCHES IT
    @Query("SELECT p FROM ParticipantEntity p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
            "LOWER(p.designation) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
            "LOWER(p.organization) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "ORDER BY p.eventDate")
    List<ParticipantEntity> findByNameOrDesignationOrOrganization(String name);

    Optional<ParticipantEntity> findByNameAndDesignationAndOrganization(String participantName, String designation, String organization);
}
