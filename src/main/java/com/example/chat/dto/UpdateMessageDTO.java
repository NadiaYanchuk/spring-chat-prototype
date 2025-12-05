package com.example.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class UpdateMessageDTO {
    @NotNull(message = "Recipient ID cannot be null")
    private Long recipient;

    @NotBlank(message = "Message text cannot be blank")
    private String text;
}
