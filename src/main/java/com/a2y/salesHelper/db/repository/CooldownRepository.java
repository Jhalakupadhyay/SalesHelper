package com.a2y.salesHelper.db.repository;

import com.a2y.salesHelper.db.entity.CooldownEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CooldownRepository extends JpaRepository<CooldownEntity,Long> {
}
