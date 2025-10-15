package com.example.chat.config;

import com.example.chat.entity.MessageEntity;
import com.example.chat.entity.RoomEntity;
import com.example.chat.entity.UserEntity;
import com.example.chat.service.MessageEntityService;
import com.example.chat.service.RoomEntityService;
import com.example.chat.service.UserEntityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {
    
    private final UserEntityService userService;
    private final RoomEntityService roomService;
    private final MessageEntityService messageService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting data initialization...");
        
        // Создаем тестовых пользователей
        createTestUsers();
        
        // Создаем тестовые комнаты
        createTestRooms();
        
        // Создаем тестовые сообщения
        createTestMessages();
        
        log.info("Data initialization completed!");
    }
    
    private void createTestUsers() {
        log.info("Creating test users...");
        
        try {
            userService.createUser("admin", "admin123", "admin@chat.com");
            userService.createUser("alice", "alice123", "alice@example.com");
            userService.createUser("bob", "bob123", "bob@example.com");
            userService.createUser("charlie", "charlie123", "charlie@example.com");
            
            log.info("Test users created successfully");
        } catch (Exception e) {
            log.warn("Some test users already exist or error occurred: {}", e.getMessage());
        }
    }
    
    private void createTestRooms() {
        log.info("Creating test rooms...");
        
        try {
            UserEntity admin = userService.findByUsername("admin").orElseThrow();
            UserEntity alice = userService.findByUsername("alice").orElseThrow();
            UserEntity bob = userService.findByUsername("bob").orElseThrow();
            
            // Создаем публичные комнаты
            roomService.createPublicRoom("General", admin);
            roomService.createPublicRoom("Random", admin);
            roomService.createPublicRoom("Tech Talk", alice);
            
            // Создаем групповую комнату
            roomService.createGroupRoom("Spring Developers", admin);
            
            // Создаем приватную комнату
            roomService.createPrivateRoom(alice, bob);
            
            log.info("Test rooms created successfully");
        } catch (Exception e) {
            log.warn("Some test rooms already exist or error occurred: {}", e.getMessage());
        }
    }
    
    private void createTestMessages() {
        log.info("Creating test messages...");
        
        try {
            UserEntity admin = userService.findByUsername("admin").orElseThrow();
            UserEntity alice = userService.findByUsername("alice").orElseThrow();
            UserEntity bob = userService.findByUsername("bob").orElseThrow();
            
            RoomEntity generalRoom = roomService.findByName("General").orElseThrow();
            RoomEntity techRoom = roomService.findByName("Tech Talk").orElseThrow();
            
            // Сообщения в General комнате
            messageService.sendMessage(generalRoom, admin, "Добро пожаловать в общий чат!");
            messageService.sendMessage(generalRoom, alice, "Привет всем! 👋");
            messageService.sendMessage(generalRoom, bob, "Отличная система чата!");
            
            // Сообщения в Tech Talk комнате
            messageService.sendMessage(techRoom, alice, "Обсуждаем Spring Boot здесь");
            messageService.sendMessage(techRoom, admin, "Отличная тема для обсуждения");
            
            // Системное сообщение
            messageService.sendSystemMessage(generalRoom, admin, "Система чата запущена", MessageEntity.MessageType.SYSTEM);
            
            log.info("Test messages created successfully");
        } catch (Exception e) {
            log.warn("Error creating test messages: {}", e.getMessage());
        }
    }
}