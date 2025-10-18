package com.example.chat.controller;

import com.example.chat.entity.MessageEntity;
import com.example.chat.entity.RoomEntity;
import com.example.chat.entity.UserEntity;
import com.example.chat.service.MessageEntityService;
import com.example.chat.service.RoomEntityService;
import com.example.chat.service.UserEntityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
@Slf4j
public class ChatEntityController {
    
    private final UserEntityService userService;
    private final RoomEntityService roomService;
    private final MessageEntityService messageService;

    // === USER ENDPOINTS ===
    
    @PostMapping("/users/register")
    public ResponseEntity<UserEntity> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserEntity user = userService.createUser(request.getUsername(), request.getPassword(), request.getEmail());
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @GetMapping("/users/online")
    public ResponseEntity<List<UserEntity>> getOnlineUsers() {
        return ResponseEntity.ok(userService.getOnlineUsers());
    }
    
    @GetMapping("/users/{id}")
    public ResponseEntity<UserEntity> getUser(@PathVariable Long id) {
        UserEntity user = userService.findById(id);
        return ResponseEntity.ok(user);
    }
    
    @PostMapping("/users/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        boolean available = !userService.isUsernameTaken(username);
        return ResponseEntity.ok(Map.of("available", available));
    }
    
    @PostMapping("/users/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        boolean available = !userService.isEmailTaken(email);
        return ResponseEntity.ok(Map.of("available", available));
    }

    // === ROOM ENDPOINTS ===
    
    @PostMapping("/rooms/public")
    public ResponseEntity<RoomEntity> createPublicRoom(@RequestBody CreateRoomRequest request) {
        UserEntity creator = userService.findById(request.getCreatorId());
        RoomEntity room = roomService.createPublicRoom(request.getName(), creator);
        return ResponseEntity.ok(room);
    }
    
    @PostMapping("/rooms/private")
    public ResponseEntity<RoomEntity> createPrivateRoom(@RequestBody CreatePrivateRoomRequest request) {
        UserEntity user1 = userService.findById(request.getUser1Id());
        UserEntity user2 = userService.findById(request.getUser2Id());
        RoomEntity room = roomService.createPrivateRoom(user1, user2);
        return ResponseEntity.ok(room);
    }
    
    @PostMapping("/rooms/group")
    public ResponseEntity<RoomEntity> createGroupRoom(@RequestBody CreateRoomRequest request) {
        UserEntity creator = userService.findById(request.getCreatorId());
        RoomEntity room = roomService.createGroupRoom(request.getName(), creator);
        return ResponseEntity.ok(room);
    }
    
    @GetMapping("/rooms")
    public ResponseEntity<List<RoomEntity>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }
    
    @GetMapping("/rooms/public")
    public ResponseEntity<List<RoomEntity>> getPublicRooms() {
        return ResponseEntity.ok(roomService.getActivePublicRooms());
    }
    
    @GetMapping("/rooms/{id}")
    public ResponseEntity<RoomEntity> getRoom(@PathVariable Long id) {
        RoomEntity room = roomService.findById(id);
        return ResponseEntity.ok(room);
    }
    
    @GetMapping("/rooms/user/{userId}")
    public ResponseEntity<List<RoomEntity>> getUserRooms(@PathVariable Long userId) {
        UserEntity user = userService.findById(userId);
        return ResponseEntity.ok(roomService.getUserRooms(user));
    }

    // === MESSAGE ENDPOINTS ===
    
    @PostMapping("/messages")
    public ResponseEntity<MessageEntity> sendMessage(@RequestBody SendMessageRequest request) {
        RoomEntity room = roomService.findById(request.getRoomId());
        UserEntity user = userService.findById(request.getUserId());
        MessageEntity message = messageService.sendMessage(room, user, request.getText());
        return ResponseEntity.ok(message);
    }
    
    @GetMapping("/messages/room/{roomId}")
    public ResponseEntity<List<MessageEntity>> getRoomMessages(@PathVariable Long roomId,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "50") int size) {
        RoomEntity room = roomService.findById(roomId);
        Page<MessageEntity> messages = messageService.getMessagesInRoom(room, PageRequest.of(page, size));
        return ResponseEntity.ok(messages.getContent());
    }
    
    @GetMapping("/messages/room/{roomId}/recent")
    public ResponseEntity<List<MessageEntity>> getRecentRoomMessages(@PathVariable Long roomId) {
        RoomEntity room = roomService.findById(roomId);
        return ResponseEntity.ok(messageService.getRecentMessagesInRoom(room));
    }
    
    @GetMapping("/messages/user/{userId}")
    public ResponseEntity<List<MessageEntity>> getUserMessages(@PathVariable Long userId) {
        UserEntity user = userService.findById(userId);
        return ResponseEntity.ok(messageService.getUserMessages(user));
    }
    
    @PutMapping("/messages/{messageId}")
    public ResponseEntity<MessageEntity> editMessage(@PathVariable Long messageId,
                                                     @RequestBody EditMessageRequest request) {
        UserEntity editor = userService.findById(request.getEditorId());
        MessageEntity message = messageService.editMessage(messageId, request.getNewText(), editor);
        return ResponseEntity.ok(message);
    }
    
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId,
                                             @RequestParam Long deleterId) {
        UserEntity deleter = userService.findById(deleterId);
        messageService.deleteMessage(messageId, deleter);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/messages/search")
    public ResponseEntity<List<MessageEntity>> searchMessages(@RequestParam String text) {
        return ResponseEntity.ok(messageService.searchMessages(text));
    }

    // === STATISTICS ENDPOINTS ===
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        return ResponseEntity.ok(Map.of(
            "totalUsers", userService.getAllUsers().size(),
            "onlineUsers", userService.getOnlineUserCount(),
            "activeUsers", userService.getActiveUserCount(),
            "totalRooms", roomService.getAllRooms().size(),
            "activeRooms", roomService.getActiveRooms().size(),
            "publicRooms", roomService.getActivePublicRooms().size(),
            "totalMessages", messageService.getAllMessages().size()
        ));
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "Spring Chat Entity Service",
            "timestamp", java.time.LocalDateTime.now().toString(),
            "database", "H2 In-Memory"
        ));
    }

    // === REQUEST DTOs ===
    
    public static class CreateUserRequest {
        private String username;
        private String password;
        private String email;
        
        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
    
    public static class CreateRoomRequest {
        private String name;
        private Long creatorId;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Long getCreatorId() { return creatorId; }
        public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
    }
    
    public static class CreatePrivateRoomRequest {
        private Long user1Id;
        private Long user2Id;
        
        // Getters and Setters
        public Long getUser1Id() { return user1Id; }
        public void setUser1Id(Long user1Id) { this.user1Id = user1Id; }
        public Long getUser2Id() { return user2Id; }
        public void setUser2Id(Long user2Id) { this.user2Id = user2Id; }
    }
    
    public static class SendMessageRequest {
        private Long roomId;
        private Long userId;
        private String text;
        
        // Getters and Setters
        public Long getRoomId() { return roomId; }
        public void setRoomId(Long roomId) { this.roomId = roomId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }
    
    public static class EditMessageRequest {
        private String newText;
        private Long editorId;
        
        // Getters and Setters
        public String getNewText() { return newText; }
        public void setNewText(String newText) { this.newText = newText; }
        public Long getEditorId() { return editorId; }
        public void setEditorId(Long editorId) { this.editorId = editorId; }
    }
}