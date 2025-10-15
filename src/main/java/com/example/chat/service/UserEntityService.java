package com.example.chat.service;

import com.example.chat.entity.UserEntity;
import com.example.chat.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserEntityService {
    
    private final UserEntityRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserEntity createUser(String username, String password, String email) {
        log.info("Creating new user with username: {}", username);
        
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("User with username '" + username + "' already exists");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email '" + email + "' already exists");
        }
        
        UserEntity user = new UserEntity(username, passwordEncoder.encode(password), email);
        UserEntity savedUser = userRepository.save(user);
        
        log.info("User created successfully with ID: {}", savedUser.getId());
        return savedUser;
    }
    
    public UserEntity connectUser(String username, String sessionId) {
        log.info("Connecting user {} with session {}", username, sessionId);
        
        Optional<UserEntity> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found: " + username);
        }
        
        UserEntity user = userOpt.get();
        user.setSessionId(sessionId);
        user.setStatus(UserEntity.UserStatus.ONLINE);
        user.setIsOnline(true);
        user.setLastActivity(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    public void disconnectUser(String sessionId) {
        log.info("Disconnecting user with session: {}", sessionId);
        
        Optional<UserEntity> userOpt = userRepository.findBySessionId(sessionId);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            user.setStatus(UserEntity.UserStatus.OFFLINE);
            user.setIsOnline(false);
            user.setSessionId(null);
            user.setLastActivity(LocalDateTime.now());
            userRepository.save(user);
            
            log.info("User {} disconnected", user.getUsername());
        }
    }
    
    public void updateUserActivity(String sessionId) {
        userRepository.updateLastActivity(sessionId, LocalDateTime.now());
    }
    
    public void updateUserStatus(String sessionId, UserEntity.UserStatus status) {
        userRepository.updateUserStatus(sessionId, status);
    }
    
    public List<UserEntity> getOnlineUsers() {
        return userRepository.findOnlineUsers();
    }
    
    public List<UserEntity> getActiveUsers() {
        return userRepository.findActiveUsers();
    }
    
    public long getOnlineUserCount() {
        return userRepository.countOnlineUsers();
    }
    
    public long getActiveUserCount() {
        return userRepository.countActiveUsers();
    }
    
    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<UserEntity> findBySessionId(String sessionId) {
        return userRepository.findBySessionId(sessionId);
    }
    
    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public UserEntity findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
    }
    
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }
    
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        userRepository.deleteById(id);
    }
}