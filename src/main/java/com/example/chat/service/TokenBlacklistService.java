package com.example.chat.service;

import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {
    private final Set<String> blacklist = ConcurrentHashMap.newKeySet();

    public void blacklist(String username) {
        blacklist.add(username);
    }

    public boolean isBlacklisted(String username) {
        return blacklist.contains(username);
    }

    public void remove(String username) {
    blacklist.remove(username);
}
}