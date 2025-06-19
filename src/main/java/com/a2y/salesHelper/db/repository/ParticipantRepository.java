package com.a2y.salesHelper.db.repository;


import com.a2y.salesHelper.db.entity.ParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface ParticipantRepository extends JpaRepository<ParticipantEntity, Long> {

    Boolean existsByNameAndDesignationAndOrganization(String name, String designation,String organization);

    @Query("SELECT p FROM ParticipantEntity p")
    List<ParticipantEntity> getAll();

    List<ParticipantEntity> findByNameContainingIgnoreCase(String name);
}
