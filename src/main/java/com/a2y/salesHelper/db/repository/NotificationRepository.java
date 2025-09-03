package com.a2y.salesHelper.db.repository;

import com.a2y.salesHelper.db.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END " +
            "FROM NotificationEntity n JOIN n.participantIds p " +
            "WHERE p = :participantId") boolean existsByParticipantId(@Param("participantId") Long participantId);
}
