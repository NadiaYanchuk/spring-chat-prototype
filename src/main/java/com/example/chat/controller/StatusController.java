package com.example.chat.controller;

import com.example.chat.component.WebSocketEventListener;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@AllArgsConstructor
public class StatusController {

    private final WebSocketEventListener webSocketEventListener;

    @GetMapping("/getonlineusers")
    public Set<String> getOnlineUsers() {
        return webSocketEventListener.getOnlineUsers();
    }
}