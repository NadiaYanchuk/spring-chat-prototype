package com.example.chat.controller;

import com.example.chat.model.Message;
import com.example.chat.model.User;
import com.example.chat.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
public class ChatController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private UserService userService;

    // WebSocket endpoints
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public Message sendMessage(@Valid @Payload Message chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now());
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public Message addUser(@Payload Message chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // Добавляем имя пользователя в атрибуты WebSocket сессии
        String username = chatMessage.getFrom();
        String sessionId = headerAccessor.getSessionId();
        
        headerAccessor.getSessionAttributes().put("username", username);
        
        // Сохраняем пользователя в базе данных
        userService.addUser(username, sessionId);
        
        // Создаем сообщение о присоединении
        Message joinMessage = new Message(username, username + " присоединился к чату!", Message.MessageType.JOIN);
        joinMessage.setTimestamp(LocalDateTime.now());
        
        // Отправляем статистику пользователей
        sendUserStats();
        
        return joinMessage;
    }

    // Старый endpoint для обратной совместимости
    @MessageMapping("/send")
    @SendTo("/topic/messages")
    public Message sendMessageLegacy(Message message) {
        message.setTimestamp(LocalDateTime.now());
        return message;
    }

    // REST API endpoints
    @GetMapping("/api/chat/users")
    public ResponseEntity<List<User>> getOnlineUsers() {
        List<User> onlineUsers = userService.getOnlineUsers();
        return ResponseEntity.ok(onlineUsers);
    }

    @GetMapping("/api/chat/stats")
    public ResponseEntity<Map<String, Object>> getChatStats() {
        long onlineCount = userService.getOnlineUserCount();
        return ResponseEntity.ok(Map.of(
            "onlineUsers", onlineCount,
            "serverTime", LocalDateTime.now().toString()
        ));
    }

    @PostMapping("/api/chat/users/check")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        boolean isAvailable = !userService.isUsernameTaken(username);
        return ResponseEntity.ok(Map.of("available", isAvailable));
    }

    @PostMapping("/api/chat/message")
    public ResponseEntity<String> sendRestMessage(@Valid @RequestBody Message message) {
        // Отправляем сообщение через WebSocket
        message.setTimestamp(LocalDateTime.now());
        messagingTemplate.convertAndSend("/topic/public", message);
        
        return ResponseEntity.ok("Сообщение отправлено");
    }

    @GetMapping("/api/chat/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "Spring Chat Service",
            "timestamp", LocalDateTime.now().toString()
        ));
    }

    private void sendUserStats() {
        long onlineCount = userService.getOnlineUserCount();
        messagingTemplate.convertAndSend("/topic/stats", 
            Map.of("onlineUsers", onlineCount));
    }
}
