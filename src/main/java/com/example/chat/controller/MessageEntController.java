package com.example.chat.controller;

import com.example.chat.constants.WebSocketDestinations;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public void sendMessage(@DestinationVariable Long recipient, @Valid MessageDTO messageDto, Principal principal) {
        if (!userService.ifExistById(recipient)) {
            return;
        }

        UserEntity sender = userService.findByUsername(principal.getName());

        MessageEntity message = new MessageEntity();
        message.setRoom(roomService.findByUsersIds(sender.getId(), recipient));
        message.setUser(sender);
        message.setText(messageDto.getText().trim());
        message.setTimestamp(messageDto.getTimestamp());

        message = messageService.save(message);

        MessagesDataDTO messageData = MessagesDataDTO.getMessageDataDtoFromMessageEntity(message);

        simpMessagingTemplate.convertAndSend(
                WebSocketDestinations.MESSAGES_TOPIC + recipient,
                messageData
        );

        simpMessagingTemplate.convertAndSend(
                WebSocketDestinations.MESSAGES_TOPIC + sender.getId(),
                messageData
        );
    }

    @PutMapping("/updatemessage/{messageId}")
    @Transactional
    public ResponseEntity<Void> updateMessage(@PathVariable Long messageId,
                                              @Valid @RequestBody UpdateMessageDTO messageDTO,
                                              Principal principal) {
        UserEntity user = userService.findByUsername(principal.getName());
        MessageEntity message = messageService.findById(messageId);

        if (message == null) {
            return ResponseEntity.notFound().build();
        }

        if (!message.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        message.setText(messageDTO.getText().trim());
        message = messageService.save(message);

        Long user1Id = message.getRoom().getUser1().getId();
        Long user2Id = message.getRoom().getUser2().getId();

        MessagesDataDTO messageData = MessagesDataDTO.getMessageDataDtoFromMessageEntity(message);

        simpMessagingTemplate.convertAndSend(
                WebSocketDestinations.UPDATE_MESSAGE_TOPIC + user1Id,
                messageData
        );

        simpMessagingTemplate.convertAndSend(
                WebSocketDestinations.UPDATE_MESSAGE_TOPIC + user2Id,
                messageData
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deletemessage/{messageId}")
    @Transactional
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId, Principal principal) {
        UserEntity user = userService.findByUsername(principal.getName());
        MessageEntity message = messageService.findById(messageId);

        if (message == null) {
            return ResponseEntity.notFound().build();
        }

        if (!message.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Long user1Id = message.getRoom().getUser1().getId();
        Long user2Id = message.getRoom().getUser2().getId();

        MessagesDataDTO messageData = MessagesDataDTO.getMessageDataDtoFromMessageEntity(message);

        messageService.delete(message);

        simpMessagingTemplate.convertAndSend(
                WebSocketDestinations.DELETE_MESSAGE_TOPIC + user1Id,
                messageData
        );

        simpMessagingTemplate.convertAndSend(
                WebSocketDestinations.DELETE_MESSAGE_TOPIC + user2Id,
                messageData
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getmessages")
    public List<MessagesDataDTO> getPrivateMessages(Principal principal, @RequestParam Long recipient) {
        UserEntity currentUser = userService.findByUsername(principal.getName());

        if (!userService.ifExistById(recipient)) {
            return List.of();
        }

        return messageService.findMessagesByUsersId(currentUser.getId(), recipient).stream()
                .map(MessagesDataDTO::getMessageDataDtoFromMessageEntity)
                .toList();
    }
}
