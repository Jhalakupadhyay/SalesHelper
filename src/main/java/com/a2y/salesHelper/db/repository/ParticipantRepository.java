package com.a2y.salesHelper.db.repository;


import com.a2y.salesHelper.db.entity.ParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<ParticipantEntity, Long> {

    Boolean existsByNameAndDesignationAndOrganization(String name, String designation,String organization);
}
