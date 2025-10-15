package com.example.chat.repository;

import com.example.chat.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
    
    Optional<UserEntity> findByUsername(String username);
    
    Optional<UserEntity> findByEmail(String email);
    
    Optional<UserEntity> findBySessionId(String sessionId);
    
    List<UserEntity> findByStatus(UserEntity.UserStatus status);
    
    @Query("SELECT u FROM UserEntity u WHERE u.isOnline = true ORDER BY u.lastActivity DESC")
    List<UserEntity> findOnlineUsers();
    
    @Query("SELECT u FROM UserEntity u WHERE u.status = 'ONLINE' ORDER BY u.joinTime DESC")
    List<UserEntity> findActiveUsers();
    
    @Modifying
    @Query("UPDATE UserEntity u SET u.status = :status WHERE u.sessionId = :sessionId")
    void updateUserStatus(@Param("sessionId") String sessionId, @Param("status") UserEntity.UserStatus status);
    
    @Modifying
    @Query("UPDATE UserEntity u SET u.lastActivity = :lastActivity WHERE u.sessionId = :sessionId")
    void updateLastActivity(@Param("sessionId") String sessionId, @Param("lastActivity") LocalDateTime lastActivity);
    
    @Modifying
    @Query("UPDATE UserEntity u SET u.isOnline = :isOnline WHERE u.sessionId = :sessionId")
    void updateOnlineStatus(@Param("sessionId") String sessionId, @Param("isOnline") Boolean isOnline);
    
    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.isOnline = true")
    long countOnlineUsers();
    
    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.status = 'ONLINE'")
    long countActiveUsers();
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    void deleteBySessionId(String sessionId);
}