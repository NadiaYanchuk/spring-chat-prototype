package com.example.chat.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Set;
import java.util.Collections;
import java.security.Principal;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    // Храним всех онлайн-пользователей
    private final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();

    public WebSocketEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
public void handleConnect(SessionConnectedEvent event) {
    Principal user = event.getUser();
    if (user != null) {
        log.info("User connected: {}", user.getName());
        onlineUsers.add(user.getName());
        messagingTemplate.convertAndSend("/topic/status", user.getName() + ":online");
    }
}
    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        Principal user = event.getUser();
        if (user != null) {
            log.info("User disconnected: {}", user.getName());
            onlineUsers.remove(user.getName());
            messagingTemplate.convertAndSend("/topic/status", user.getName() + ":offline");
        }
    }

    public Set<String> getOnlineUsers() {
    return Collections.unmodifiableSet(onlineUsers);
}
}