package com.a2y.salesHelper.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.a2y.salesHelper.db.entity.ClientEntity;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {

    // Tenant filtering methods
    List<ClientEntity> findByTenantId(Long tenantId);
    java.util.Optional<ClientEntity> findByTenantIdAndOrgId(Long tenantId,Long orgId);
}
