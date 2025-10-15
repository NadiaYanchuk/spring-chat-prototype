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

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageEntityRepository extends JpaRepository<MessageEntity, Long> {
    
    // Найти сообщения в комнате с пагинацией
    Page<MessageEntity> findByRoomOrderByTimestampDesc(RoomEntity room, Pageable pageable);
    
    // Найти последние сообщения в комнате
    List<MessageEntity> findTop50ByRoomOrderByTimestampDesc(RoomEntity room);
    
    // Найти сообщения от пользователя
    List<MessageEntity> findByUserOrderByTimestampDesc(UserEntity user);
    
    // Найти сообщения в комнате от пользователя
    List<MessageEntity> findByRoomAndUserOrderByTimestampDesc(RoomEntity room, UserEntity user);
    
    // Найти сообщения по типу
    List<MessageEntity> findByMessageTypeOrderByTimestampDesc(MessageEntity.MessageType messageType);
    
    // Найти сообщения в диапазоне времени
    @Query("SELECT m FROM MessageEntity m WHERE m.room = :room AND m.timestamp BETWEEN :startTime AND :endTime ORDER BY m.timestamp DESC")
    List<MessageEntity> findMessagesInTimeRange(@Param("room") RoomEntity room, 
                                               @Param("startTime") LocalDateTime startTime, 
                                               @Param("endTime") LocalDateTime endTime);
    
    // Найти последнее сообщение в комнате
    @Query("SELECT m FROM MessageEntity m WHERE m.room = :room ORDER BY m.timestamp DESC LIMIT 1")
    MessageEntity findLastMessageInRoom(@Param("room") RoomEntity room);
    
    // Подсчет сообщений от пользователя
    long countByUser(UserEntity user);
    
    // Подсчет сообщений в комнате
    long countByRoom(RoomEntity room);
    
    // Найти редактированные сообщения
    List<MessageEntity> findByIsEditedTrueOrderByEditedAtDesc();
    
    // Поиск по тексту сообщения (простой поиск)
    @Query("SELECT m FROM MessageEntity m WHERE LOWER(m.text) LIKE LOWER(CONCAT('%', :searchText, '%')) ORDER BY m.timestamp DESC")
    List<MessageEntity> searchByText(@Param("searchText") String searchText);
}