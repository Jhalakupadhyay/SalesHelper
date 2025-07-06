package com.a2y.salesHelper.db.repository;

import com.a2y.salesHelper.db.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByEmail(String email);

    //query to update the

}
