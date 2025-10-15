package com.example.chat.service;

import com.example.chat.entity.MessageEntity;
import com.example.chat.entity.RoomEntity;
import com.example.chat.entity.UserEntity;
import com.example.chat.repository.MessageEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MessageEntityService {
    
    private final MessageEntityRepository messageRepository;
    
    public MessageEntity sendMessage(RoomEntity room, UserEntity user, String text) {
        log.info("Sending message from user {} to room {}", user.getUsername(), room.getName());
        
        MessageEntity message = new MessageEntity(room, user, text);
        MessageEntity savedMessage = messageRepository.save(message);
        
        log.info("Message sent with ID: {}", savedMessage.getId());
        return savedMessage;
    }
    
    public MessageEntity sendSystemMessage(RoomEntity room, UserEntity user, String text, MessageEntity.MessageType messageType) {
        log.info("Sending system message type {} from user {} to room {}", messageType, user.getUsername(), room.getName());
        
        MessageEntity message = new MessageEntity(room, user, text, messageType);
        MessageEntity savedMessage = messageRepository.save(message);
        
        log.info("System message sent with ID: {}", savedMessage.getId());
        return savedMessage;
    }
    
    public MessageEntity editMessage(Long messageId, String newText, UserEntity editor) {
        log.info("Editing message with ID: {} by user {}", messageId, editor.getUsername());
        
        MessageEntity message = findById(messageId);
        
        // Проверяем, что редактировать может только автор сообщения
        if (!message.getUser().getId().equals(editor.getId())) {
            throw new IllegalArgumentException("User can only edit their own messages");
        }
        
        message.editMessage(newText);
        MessageEntity updatedMessage = messageRepository.save(message);
        
        log.info("Message edited successfully");
        return updatedMessage;
    }
    
    public MessageEntity findById(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Message not found with ID: " + id));
    }
    
    public Page<MessageEntity> getMessagesInRoom(RoomEntity room, Pageable pageable) {
        return messageRepository.findByRoomOrderByTimestampDesc(room, pageable);
    }
    
    public List<MessageEntity> getRecentMessagesInRoom(RoomEntity room) {
        return messageRepository.findTop50ByRoomOrderByTimestampDesc(room);
    }
    
    public List<MessageEntity> getUserMessages(UserEntity user) {
        return messageRepository.findByUserOrderByTimestampDesc(user);
    }
    
    public List<MessageEntity> getUserMessagesInRoom(RoomEntity room, UserEntity user) {
        return messageRepository.findByRoomAndUserOrderByTimestampDesc(room, user);
    }
    
    public List<MessageEntity> getMessagesByType(MessageEntity.MessageType messageType) {
        return messageRepository.findByMessageTypeOrderByTimestampDesc(messageType);
    }
    
    public List<MessageEntity> getMessagesInTimeRange(RoomEntity room, LocalDateTime startTime, LocalDateTime endTime) {
        return messageRepository.findMessagesInTimeRange(room, startTime, endTime);
    }
    
    public MessageEntity getLastMessageInRoom(RoomEntity room) {
        return messageRepository.findLastMessageInRoom(room);
    }
    
    public long getUserMessageCount(UserEntity user) {
        return messageRepository.countByUser(user);
    }
    
    public long getRoomMessageCount(RoomEntity room) {
        return messageRepository.countByRoom(room);
    }
    
    public List<MessageEntity> getEditedMessages() {
        return messageRepository.findByIsEditedTrueOrderByEditedAtDesc();
    }
    
    public List<MessageEntity> searchMessages(String searchText) {
        return messageRepository.searchByText(searchText);
    }
    
    public void deleteMessage(Long messageId, UserEntity deleter) {
        log.info("Deleting message with ID: {} by user {}", messageId, deleter.getUsername());
        
        MessageEntity message = findById(messageId);
        
        // Проверяем, что удалять может только автор сообщения или создатель комнаты
        if (!message.getUser().getId().equals(deleter.getId()) && 
            !message.getRoom().getCreator().getId().equals(deleter.getId())) {
            throw new IllegalArgumentException("User can only delete their own messages or room creator can delete any message");
        }
        
        messageRepository.deleteById(messageId);
        log.info("Message deleted successfully");
    }
    
    public List<MessageEntity> getAllMessages() {
        return messageRepository.findAll();
    }
}