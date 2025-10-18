package com.example.chat.handler;

import com.example.chat.service.UserEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

import java.util.Map;

@Component
public class WebSocketEventHandler {

    @Autowired
    private UserEntityService userService;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        // Логируем подключение
        System.out.println("Новое WebSocket подключение: " + sessionId);

        // Отправляем статистику подключенных пользователей
        sendUserStats();
    }

    private void sendUserStats() {
        long onlineCount = userService.getOnlineUserCount();
        messagingTemplate.convertAndSend("/topic/stats", 
            Map.of("onlineUsers", onlineCount));
    }

    private Map<String, Object> createSystemMessage(String text) {
        return Map.of(
            "from", "Система",
            "text", text,
            "type", "SYSTEM",
            "timestamp", java.time.LocalDateTime.now().toString()
        );
    }
}