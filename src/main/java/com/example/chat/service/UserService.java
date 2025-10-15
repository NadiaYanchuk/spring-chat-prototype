package com.example.chat.service;

import com.example.chat.model.User;
import com.example.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User addUser(String username, String sessionId) {
        // Проверяем, не существует ли уже пользователь с таким именем
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            // Обновляем сессию существующего пользователя
            User user = existingUser.get();
            user.setSessionId(sessionId);
            user.setStatus(User.UserStatus.ONLINE);
            user.setLastActivity(LocalDateTime.now());
            return userRepository.save(user);
        }
        
        // Создаем нового пользователя
        User newUser = new User(username, sessionId);
        return userRepository.save(newUser);
    }
    
    public void removeUser(String sessionId) {
        userRepository.deleteBySessionId(sessionId);
    }
    
    public void updateUserStatus(String sessionId, User.UserStatus status) {
        userRepository.updateUserStatus(sessionId, status);
    }
    
    public void updateLastActivity(String sessionId) {
        userRepository.updateLastActivity(sessionId, LocalDateTime.now());
    }
    
    public List<User> getOnlineUsers() {
        return userRepository.findOnlineUsers();
    }
    
    public long getOnlineUserCount() {
        return userRepository.countOnlineUsers();
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findBySessionId(String sessionId) {
        return userRepository.findBySessionId(sessionId);
    }
    
    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}