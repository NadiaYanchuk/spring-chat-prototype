package com.example.chat.dto;

import com.example.chat.entity.UserEntity;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private Boolean isDeleted;
    private String role;
    private LocalDateTime joinTime;
    private LocalDateTime bannedUntil;

    public static UserDTO getUserDtoFromUser(UserEntity user) {
       return new UserDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getIsDeleted(),
            user.getRole(),
            user.getJoinTime(),
            user.getBannedUntil()
        );
    }
}
