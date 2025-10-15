package com.example.chat.repository;

import com.example.chat.entity.RoomEntity;
import com.example.chat.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomEntityRepository extends JpaRepository<RoomEntity, Long> {
    
    Optional<RoomEntity> findByName(String name);
    
    List<RoomEntity> findByRoomType(RoomEntity.RoomType roomType);
    
    List<RoomEntity> findByIsActiveTrue();
    
    List<RoomEntity> findByCreator(UserEntity creator);
    
    // Найти приватную комнату между двумя пользователями
    @Query("SELECT r FROM RoomEntity r WHERE r.roomType = 'PRIVATE' AND " +
           "((r.user1 = :user1 AND r.user2 = :user2) OR (r.user1 = :user2 AND r.user2 = :user1))")
    Optional<RoomEntity> findPrivateRoomBetweenUsers(@Param("user1") UserEntity user1, @Param("user2") UserEntity user2);
    
    // Найти все комнаты пользователя
    @Query("SELECT r FROM RoomEntity r WHERE r.creator = :user OR r.user1 = :user OR r.user2 = :user")
    List<RoomEntity> findUserRooms(@Param("user") UserEntity user);
    
    // Найти активные публичные комнаты
    @Query("SELECT r FROM RoomEntity r WHERE r.roomType = 'PUBLIC' AND r.isActive = true ORDER BY r.createdAt DESC")
    List<RoomEntity> findActivePublicRooms();
    
    // Подсчет сообщений в комнате
    @Query("SELECT COUNT(m) FROM MessageEntity m WHERE m.room = :room")
    long countMessagesInRoom(@Param("room") RoomEntity room);
    
    boolean existsByName(String name);
}