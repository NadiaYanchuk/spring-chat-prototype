package com.example.chat.service;

import com.example.chat.entity.RoomEntity;
import com.example.chat.entity.UserEntity;
import com.example.chat.repository.RoomEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoomEntityService {
    
    private final RoomEntityRepository roomRepository;
    
    public RoomEntity createPublicRoom(String roomName, UserEntity creator) {
        log.info("Creating public room '{}' by user {}", roomName, creator.getUsername());
        
        if (roomRepository.existsByName(roomName)) {
            throw new IllegalArgumentException("Room with name '" + roomName + "' already exists");
        }
        
        RoomEntity room = new RoomEntity(roomName, RoomEntity.RoomType.PUBLIC, creator);
        RoomEntity savedRoom = roomRepository.save(room);
        
        log.info("Public room created with ID: {}", savedRoom.getId());
        return savedRoom;
    }
    
    public RoomEntity createPrivateRoom(UserEntity user1, UserEntity user2) {
        log.info("Creating private room between {} and {}", user1.getUsername(), user2.getUsername());
        
        // Проверяем, существует ли уже приватная комната между этими пользователями
        Optional<RoomEntity> existingRoom = roomRepository.findPrivateRoomBetweenUsers(user1, user2);
        if (existingRoom.isPresent()) {
            log.info("Private room already exists between users");
            return existingRoom.get();
        }
        
        String roomName = "Private: " + user1.getUsername() + " & " + user2.getUsername();
        RoomEntity room = new RoomEntity(roomName, user1, user2);
        RoomEntity savedRoom = roomRepository.save(room);
        
        log.info("Private room created with ID: {}", savedRoom.getId());
        return savedRoom;
    }
    
    public RoomEntity createGroupRoom(String roomName, UserEntity creator) {
        log.info("Creating group room '{}' by user {}", roomName, creator.getUsername());
        
        if (roomRepository.existsByName(roomName)) {
            throw new IllegalArgumentException("Room with name '" + roomName + "' already exists");
        }
        
        RoomEntity room = new RoomEntity(roomName, RoomEntity.RoomType.GROUP, creator);
        RoomEntity savedRoom = roomRepository.save(room);
        
        log.info("Group room created with ID: {}", savedRoom.getId());
        return savedRoom;
    }
    
    public Optional<RoomEntity> findByName(String roomName) {
        return roomRepository.findByName(roomName);
    }
    
    public RoomEntity findById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + id));
    }
    
    public List<RoomEntity> getActivePublicRooms() {
        return roomRepository.findActivePublicRooms();
    }
    
    public List<RoomEntity> getUserRooms(UserEntity user) {
        return roomRepository.findUserRooms(user);
    }
    
    public List<RoomEntity> getRoomsByType(RoomEntity.RoomType roomType) {
        return roomRepository.findByRoomType(roomType);
    }
    
    public List<RoomEntity> getActiveRooms() {
        return roomRepository.findByIsActiveTrue();
    }
    
    public List<RoomEntity> getRoomsByCreator(UserEntity creator) {
        return roomRepository.findByCreator(creator);
    }
    
    public Optional<RoomEntity> findPrivateRoomBetweenUsers(UserEntity user1, UserEntity user2) {
        return roomRepository.findPrivateRoomBetweenUsers(user1, user2);
    }
    
    public long getMessageCountInRoom(RoomEntity room) {
        return roomRepository.countMessagesInRoom(room);
    }
    
    public void deactivateRoom(Long roomId) {
        log.info("Deactivating room with ID: {}", roomId);
        RoomEntity room = findById(roomId);
        room.setIsActive(false);
        roomRepository.save(room);
    }
    
    public void activateRoom(Long roomId) {
        log.info("Activating room with ID: {}", roomId);
        RoomEntity room = findById(roomId);
        room.setIsActive(true);
        roomRepository.save(room);
    }
    
    public void deleteRoom(Long roomId) {
        log.info("Deleting room with ID: {}", roomId);
        roomRepository.deleteById(roomId);
    }
    
    public List<RoomEntity> getAllRooms() {
        return roomRepository.findAll();
    }
    
    public boolean roomExists(String roomName) {
        return roomRepository.existsByName(roomName);
    }
}