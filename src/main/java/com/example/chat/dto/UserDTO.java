package com.example.chat.dto;

import com.example.chat.entity.UserEntity;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class UserDTO {
    private Long id;
    private String username;
    private String email;

    public static UserDTO getUserDtoFromUser(UserEntity user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail());
    }
}
