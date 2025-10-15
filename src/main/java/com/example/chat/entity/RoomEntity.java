package com.example.chat.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "rooms")
public class RoomEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(name = "room_type")
    @Enumerated(EnumType.STRING)
    private RoomType roomType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    @ToString.Exclude
    private UserEntity creator;

    // Для приватных чатов (1 на 1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id")
    @ToString.Exclude
    private UserEntity user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id")
    @ToString.Exclude
    private UserEntity user2;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<MessageEntity> messages;

    public enum RoomType {
        PUBLIC,    // Публичная комната
        PRIVATE,   // Приватный чат (1 на 1)
        GROUP      // Групповой чат
    }

    public RoomEntity(String name, RoomType roomType, UserEntity creator) {
        this.name = name;
        this.roomType = roomType;
        this.creator = creator;
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    // Конструктор для приватного чата
    public RoomEntity(String name, UserEntity user1, UserEntity user2) {
        this.name = name;
        this.roomType = RoomType.PRIVATE;
        this.user1 = user1;
        this.user2 = user2;
        this.creator = user1;
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (isActive == null) {
            isActive = true;
        }
    }
}