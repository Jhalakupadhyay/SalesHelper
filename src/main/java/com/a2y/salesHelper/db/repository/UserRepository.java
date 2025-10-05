package com.a2y.salesHelper.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.a2y.salesHelper.db.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByEmail(String email);

    @Query("SELECT u FROM UserEntity u WHERE u.inviteId = :id")
    List<UserEntity> findAllById(Long id);

    // Tenant filtering methods
    List<UserEntity> findByTenantId(Long tenantId);

    UserEntity findByEmailAndTenantId(String email, Long tenantId);

    @Query("SELECT u FROM UserEntity u WHERE u.inviteId = :id AND u.tenantId = :tenantId")
    List<UserEntity> findAllByIdAndTenantId(Long id, Long tenantId);

}
