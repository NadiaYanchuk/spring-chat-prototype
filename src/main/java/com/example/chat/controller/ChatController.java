package com.example.chat.controller;

import com.example.chat.model.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/send") // клиент отправляет сюда
    @SendTo("/topic/messages") // сервер вещает всем
    public Message sendMessage(Message message) {
        return message;
    }
}
