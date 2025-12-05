package com.example.chat.controller;

import com.example.chat.constants.WebSocketDestinations;
import com.example.chat.dto.DeleteMessageDTO;
import com.example.chat.dto.MessageDTO;
import com.example.chat.dto.MessagesDataDTO;
import com.example.chat.dto.UpdateMessageDTO;
import com.example.chat.entity.MessageEntity;
import com.example.chat.entity.UserEntity;
import com.example.chat.service.MessageEntityService;
import com.example.chat.service.RoomEntityService;
import com.example.chat.service.UserEntityService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
public class MessageEntController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    private final UserEntityService userService;

    private final RoomEntityService roomService;

    private final MessageEntityService messageService;

    @MessageMapping("/chat/{recipient}")
    @Transactional
    public void sendMessage(@DestinationVariable Long recipient, MessageDTO messageDto) {
        if (!userService.ifExistById(recipient)) {
            return;
        }

        MessageEntity message = new MessageEntity();
        message.setRoom(roomService.findByUsersIds(messageDto.getSenderId(), messageDto.getRecipientId()));
        message.setUser(userService.findById(messageDto.getSenderId()));
        message.setText(messageDto.getText());
        message.setTimestamp(messageDto.getTimestamp());

        message = messageService.save(message);
        simpMessagingTemplate.convertAndSend(
                WebSocketDestinations.MESSAGES_TOPIC + recipient,
                MessagesDataDTO.getMessageDataDtoFromMessageEntity(message)
        );
    }

    @PutMapping("/updatemessage")
    public void updateMessage(@RequestParam Long timestamp, @Valid @RequestBody UpdateMessageDTO messageDTO, Principal principal) {
        UserEntity user = userService.findByUsername(principal.getName());
        MessageEntity message = messageService.findByTimestamp(timestamp);

        if (message == null || !message.getUser().equals(user)) {
            return;
        }

        message.setText(messageDTO.getText());
        messageService.save(message);

        simpMessagingTemplate.convertAndSend(
                WebSocketDestinations.UPDATE_MESSAGE_TOPIC + messageDTO.getRecipient(),
                MessagesDataDTO.getMessageDataDtoFromMessageEntity(message)
        );
    }

    @DeleteMapping("/deletemessage")
    public void deleteMessage(@Valid @RequestBody DeleteMessageDTO messageDTO) {
        MessageEntity message = messageService.findByTimestamp(messageDTO.getTimestamp());

        if (message == null) {
            return;
        }

        messageService.delete(message);

        MessagesDataDTO messageData = MessagesDataDTO.getMessageDataDtoFromMessageEntity(message);
        simpMessagingTemplate.convertAndSend(
                WebSocketDestinations.DELETE_MESSAGE_TOPIC + messageDTO.getRecipient(), 
                messageData);
        simpMessagingTemplate.convertAndSend(
                WebSocketDestinations.DELETE_MESSAGE_TOPIC + messageDTO.getPrincipal(), 
                messageData);
    }

    @GetMapping("/getmessages")
    public List<MessagesDataDTO> getPrivateMessages(@RequestParam Long sender, @RequestParam Long recipient) {
        if (!userService.ifExistById(sender) || !userService.ifExistById(recipient)) {
            return List.of();
        }

        return messageService.findMessagesByUsersId(sender, recipient).stream()
                .map(MessagesDataDTO::getMessageDataDtoFromMessageEntity)
                .toList();
    }
}
