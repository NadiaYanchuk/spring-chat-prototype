package com.example.chat.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class Message {
    
    @NotBlank(message = "Отправитель не может быть пустым")
    @Size(min = 2, max = 50, message = "Имя отправителя должно быть от 2 до 50 символов")
    private String from;
    
    @NotBlank(message = "Текст сообщения не может быть пустым")
    @Size(max = 500, message = "Сообщение не может превышать 500 символов")
    private String text;
    
    private LocalDateTime timestamp;
    private MessageType type;
    private String roomId;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }

    public Message() {
        this.timestamp = LocalDateTime.now();
        this.type = MessageType.CHAT;
    }

    public Message(String from, String text) {
        this();
        this.from = from;
        this.text = text;
    }

    public Message(String from, String text, MessageType type) {
        this(from, text);
        this.type = type;
    }

    // Getters and Setters
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return "Message{" +
                "from='" + from + '\'' +
                ", text='" + text + '\'' +
                ", timestamp=" + timestamp +
                ", type=" + type +
                ", roomId='" + roomId + '\'' +
                '}';
    }
}
