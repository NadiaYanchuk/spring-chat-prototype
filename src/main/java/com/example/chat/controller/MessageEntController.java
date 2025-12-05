package com.example.chat.controller;

import com.example.chat.dto.DeleteMessageDTO;
import com.example.chat.dto.MessageDTO;
import com.example.chat.dto.MessagesDataDTO;
import com.example.chat.dto.UpdateMessageDTO;
import com.example.chat.entity.MessageEntity;
import com.example.chat.entity.UserEntity;
import com.example.chat.service.MessageEntityService;
import com.example.chat.service.RoomEntityService;
import com.example.chat.service.UserEntityService;
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
        System.out.println("handling send message: " + messageDto + " to: " + recipient);

        MessageEntity message = new MessageEntity();

        message.setRoom(roomService.findByUsersIds(messageDto.getSenderId(), messageDto.getRecipientId()));
        message.setUser(userService.findById(messageDto.getSenderId()));
        message.setText(messageDto.getText());
        message.setTimestamp(messageDto.getTimestamp());

        if (userService.ifExistById(recipient)) {
            message = messageService.save(message);
            simpMessagingTemplate.convertAndSend(
                    "/topic/messages/" + recipient,
                    MessagesDataDTO.getMessageDataDtoFromMessageEntity(message)
            );
        }
    }

    @PutMapping("/updatemessage")
    public void updateMessage(@RequestParam Long timestamp, @RequestBody UpdateMessageDTO messageDTO, Principal principal) {
        System.out.println("handling update message: " + messageDTO + " to: " + timestamp );

        UserEntity user = userService.findByUsername(principal.getName());

        MessageEntity message = messageService.findByTimestamp(timestamp);

        if (message.getUser().equals(user)) {
            message.setText(messageDTO.getText());

            messageService.save(message);

            simpMessagingTemplate.convertAndSend(
                    "/topic/updatemessage/" + messageDTO.getRecipient(),
                    MessagesDataDTO.getMessageDataDtoFromMessageEntity(message)
            );
        }
    }

    @DeleteMapping("/deletemessage")
    public void deleteMessage(@RequestBody DeleteMessageDTO messageDTO) {
        System.out.println("delete");

        MessageEntity message = messageService.findByTimestamp(messageDTO.getTimestamp());

        if (message != null) {
            messageService.delete(message);

            simpMessagingTemplate.convertAndSend(
                    "/topic/deletemsg/" + messageDTO.getRecipient(),
                    MessagesDataDTO.getMessageDataDtoFromMessageEntity(message)
            );
            simpMessagingTemplate.convertAndSend(
                    "/topic/deletemsg/" + messageDTO.getPrincipal(),
                    MessagesDataDTO.getMessageDataDtoFromMessageEntity(message)
            );
        }
    }

    @GetMapping("/getmessages")
    public List<MessagesDataDTO> getPrivateMessages(@RequestParam Long sender, @RequestParam Long recipient) {
        boolean isExists = userService.ifExistById(sender) && userService.ifExistById(recipient);

        if (isExists) {
            System.out.println(messageService.findMessagesByUsersId(sender, recipient).stream()
                    .map(e -> e.getTimestamp().getTime())
                    .toList());
            return messageService.findMessagesByUsersId(sender, recipient).stream()
                    .map(MessagesDataDTO::getMessageDataDtoFromMessageEntity)
                    .toList();
        }

        return List.of(new MessagesDataDTO());
    }
}
