package com.a2y.salesHelper.db.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.a2y.salesHelper.db.entity.TenantEntity;

@Repository
public interface TenantRepository extends JpaRepository<TenantEntity, Long> {

    Optional<TenantEntity> findByTenantName(String tenantName);

    boolean existsByTenantName(String tenantName);
}
