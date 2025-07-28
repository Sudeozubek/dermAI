package com.example.dermAI.dao;

import com.example.dermAI.entity.Routine;
import com.example.dermAI.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface RoutineRepository extends JpaRepository<Routine, UUID> {
    Optional<Routine> findFirstByUserAndCreatedAtAfterOrderByCreatedAtDesc(User user, LocalDateTime after);
    
    // Kullanıcının tüm rutinlerini sil
    @Query("DELETE FROM Routine r WHERE r.user = :user")
    @Modifying
    @Transactional
    void deleteByUser(@Param("user") User user);

    List<Routine> findByUser(User user);
}
