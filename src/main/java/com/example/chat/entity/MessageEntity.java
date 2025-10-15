package com.example.chat.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "messages")
public class MessageEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    @ToString.Exclude
    private RoomEntity room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private UserEntity user;

    @Column(columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Message text cannot be blank")
    @Size(max = 1000, message = "Message cannot exceed 1000 characters")
    private String text;

    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    private MessageType messageType;
    
    @Column(name = "is_edited")
    private Boolean isEdited = false;
    
    @Column(name = "edited_at")
    private LocalDateTime editedAt;

    public enum MessageType {
        CHAT,      // Обычное сообщение
        JOIN,      // Пользователь присоединился
        LEAVE,     // Пользователь покинул чат
        SYSTEM     // Системное сообщение
    }

    public MessageEntity(RoomEntity room, UserEntity user, String text) {
        this.room = room;
        this.user = user;
        this.text = text;
        this.timestamp = LocalDateTime.now();
        this.messageType = MessageType.CHAT;
        this.isEdited = false;
    }

    public MessageEntity(RoomEntity room, UserEntity user, String text, MessageType messageType) {
        this.room = room;
        this.user = user;
        this.text = text;
        this.timestamp = LocalDateTime.now();
        this.messageType = messageType;
        this.isEdited = false;
    }
    
    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        if (messageType == null) {
            messageType = MessageType.CHAT;
        }
        if (isEdited == null) {
            isEdited = false;
        }
    }
    
    public void editMessage(String newText) {
        this.text = newText;
        this.isEdited = true;
        this.editedAt = LocalDateTime.now();
    }
}