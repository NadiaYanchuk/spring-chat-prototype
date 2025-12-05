package com.example.chat.dto;

import com.example.chat.entity.MessageEntity;
import lombok.*;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class MessagesDataDTO {
    private Long id;

    private RoomDTO room;

    private UserDTO user;

    private String text;

    private Timestamp timestamp;

    public static MessagesDataDTO getMessageDataDtoFromMessageEntity(MessageEntity message) {
        return new MessagesDataDTO(
                message.getId(),
                RoomDTO.getRoomDtoFromRoom(message.getRoom()),
                UserDTO.getUserDtoFromUser(message.getUser()),
                message.getText(),
                message.getTimestamp()
        );
    }
}
