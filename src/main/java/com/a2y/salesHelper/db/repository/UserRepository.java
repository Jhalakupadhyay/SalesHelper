package com.a2y.salesHelper.db.repository;

import com.a2y.salesHelper.db.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByEmail(String email);

    @Query("SELECT u FROM UserEntity u WHERE u.inviteId = :id")
    List<UserEntity> findAllById(Long id);

}
