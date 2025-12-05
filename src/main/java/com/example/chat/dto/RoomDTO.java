package com.example.chat.dto;

import com.example.chat.entity.RoomEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RoomDTO {
    private Long id;

    private UserDTO user1;

    private UserDTO user2;

    public static RoomDTO getRoomDtoFromRoom(RoomEntity room) {
        return new RoomDTO(
                room.getId(),
                UserDTO.getUserDtoFromUser(room.getUser1()),
                UserDTO.getUserDtoFromUser(room.getUser2())
        );
    }
}
