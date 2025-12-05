package com.example.chat.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class DeleteMessageDTO {
    @NotNull(message = "Principal ID cannot be null")
    private Long principal;

    @NotNull(message = "Recipient ID cannot be null")
    private Long recipient;

    @NotNull(message = "Timestamp cannot be null")
    private Long timestamp;
}
