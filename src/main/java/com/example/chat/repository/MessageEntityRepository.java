package com.example.chat.repository;

import com.example.chat.entity.MessageEntity;
import com.example.chat.entity.RoomEntity;
import com.example.chat.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageEntityRepository extends JpaRepository<MessageEntity, Long> {

    @Query("SELECT m FROM MessageEntity m WHERE m.room.id = :roomId " +
            "AND (m.user = :user OR m.room.user1 = :user OR m.room.user2 = :user)")
    List<MessageEntity> findMessagesByRoomAndUser(@Param("roomId") Long roomId, @Param("user") UserEntity user);

    @Query("SELECT m FROM MessageEntity m " +
            "JOIN FETCH m.room r " +
            "JOIN FETCH m.user u " +
            "JOIN FETCH r.user1 " +
            "JOIN FETCH r.user2 " +
            "WHERE ((r.user1.username = :currentUser AND r.user2.username = :recipient) OR " +
            "(r.user1.username = :recipient AND r.user2.username = :currentUser)) " +
            "AND (m.user.username = :currentUser OR m.user.username = :recipient) " +
            "ORDER BY m.timestamp ASC")
    List<MessageEntity> findMessagesByUsersNames(@Param("currentUser") String currentUser, @Param("recipient") String recipient);

    @Query("SELECT m FROM MessageEntity m " +
            "JOIN FETCH m.room r " +
            "JOIN FETCH m.user u " +
            "JOIN FETCH r.user1 " +
            "JOIN FETCH r.user2 " +
            "WHERE ((r.user1.id = :currentUser AND r.user2.id = :recipient) OR " +
            "(r.user1.id = :recipient AND r.user2.id = :currentUser)) " +
            "AND (m.user.id = :currentUser OR m.user.id = :recipient) " +
            "ORDER BY m.timestamp ASC")
    List<MessageEntity> findMessagesByUsersId(@Param("currentUser") Long currentUser, @Param("recipient") Long recipient);

    void deleteByTimestamp(Timestamp timestamp);

    @Query("SELECT m FROM MessageEntity m " +
            "JOIN FETCH m.room r " +
            "JOIN FETCH m.user " +
            "JOIN FETCH r.user1 " +
            "JOIN FETCH r.user2 " +
            "WHERE m.timestamp = :timestamp")
    MessageEntity findByTimestamp(@Param("timestamp") Timestamp timestamp);
}