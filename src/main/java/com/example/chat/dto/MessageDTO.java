package com.example.chat.dto;

import com.example.chat.entity.MessageEntity;
import java.time.LocalDateTime;

public record MessageDTO(
        Long id,
        Long userId,
        String text,
        LocalDateTime timestamp,
        MessageEntity.MessageType messageType,
        Boolean isEdited,
        LocalDateTime editedAt
) {
    public static MessageDTO from(MessageEntity m) {
        return new MessageDTO(
                m.getId(),
                m.getUser() != null ? m.getUser().getId() : null, // id у proxy доступен без инициализации
                m.getText(),
                m.getTimestamp(),
                m.getMessageType(),
                m.getIsEdited(),
                m.getEditedAt()
        );
    }
}
