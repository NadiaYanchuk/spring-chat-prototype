package com.example.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class MessageDTO {
    @NotNull(message = "Sender ID cannot be null")
    private Long senderId;

    @NotNull(message = "Recipient ID cannot be null")
    private Long recipientId;

    @NotBlank(message = "Message text cannot be blank")
    private String text;

    @NotNull(message = "Timestamp cannot be null")
    private Timestamp timestamp;
}
