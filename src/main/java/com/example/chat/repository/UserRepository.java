package com.example.chat.repository;

import com.example.chat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findBySessionId(String sessionId);
    
    List<User> findByStatus(User.UserStatus status);
    
    @Query("SELECT u FROM User u WHERE u.status = 'ONLINE' ORDER BY u.joinTime DESC")
    List<User> findOnlineUsers();
    
    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.sessionId = :sessionId")
    void updateUserStatus(@Param("sessionId") String sessionId, @Param("status") User.UserStatus status);
    
    @Modifying
    @Query("UPDATE User u SET u.lastActivity = :lastActivity WHERE u.sessionId = :sessionId")
    void updateLastActivity(@Param("sessionId") String sessionId, @Param("lastActivity") LocalDateTime lastActivity);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = 'ONLINE'")
    long countOnlineUsers();
    
    void deleteBySessionId(String sessionId);
}