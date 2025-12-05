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
    RoomEntity findByUser1_IdAndUser2_Id(Long user1Id, Long user2Id);

    Boolean existsByUser1_IdAndUser2_Id(Long user1Id, Long user2Id);

    @Query("SELECT c FROM RoomEntity c " +
            "JOIN FETCH c.user1 " +
            "JOIN FETCH c.user2 " +
            "WHERE (c.user1.username = :user1 AND c.user2.username = :user2) OR (c.user2.username = :user1 AND c.user1.username = :user2)")
    RoomEntity findByUser1UsernameAndUser2UsernameOrUser2UsernameAndUser1Username(@Param("user1") String user1, @Param("user2") String user2);

    @Query("SELECT c FROM RoomEntity c " +
            "JOIN FETCH c.user1 " +
            "JOIN FETCH c.user2 " +
            "WHERE (c.user1.id = :user1 AND c.user2.id = :user2) OR (c.user2.id = :user1 AND c.user1.id = :user2)")
    RoomEntity findByUser1IdAndUser2IdOrUser2IdAndUser1Id(@Param("user1") Long user1, @Param("user2") Long user2);

    @Query("SELECT r FROM RoomEntity r " +
            "JOIN FETCH r.user1 " +
            "JOIN FETCH r.user2 " +
            "WHERE r.user1.username = :username OR r.user2.username = :username")
    List<RoomEntity> findRoomsByUser(@Param("username") String username);
}