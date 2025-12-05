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
    UserEntity findByUsername(String username);

    boolean existsByUsername(String username);

    List<UserEntity> findAllByUsernameNot(String username);

    @Query("SELECT CASE " +
            "WHEN r.user1.username = :username THEN r.user2.id " +
            "WHEN r.user2.username = :username THEN r.user1.id " +
            "END " +
            "FROM RoomEntity r " +
            "WHERE r.user1.username = :username OR r.user2.username = :username")
    List<Long> findConversationParticipantIdsByUsername(@Param("username") String username);

    @Query("SELECT u FROM UserEntity u WHERE u.id <> :userId " +
            "AND u.id NOT IN (SELECT r.user1.id FROM RoomEntity r WHERE r.user2 = :user) " +
            "AND u.id NOT IN (SELECT r.user2.id FROM RoomEntity r WHERE r.user1 = :user)" +
            "AND u.username ILIKE :searchTerm%")
    List<UserEntity> findUsersWithoutRoomByUser(@Param("user") UserEntity user, @Param("userId") Long userId, @Param("searchTerm") String searchTerm);
}