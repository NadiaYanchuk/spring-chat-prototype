package com.example.chat.dto;

import lombok.*;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class MessageDTO {
    private Long senderId;

    private Long recipientId;

    private String text;

    private Timestamp timestamp;
}
