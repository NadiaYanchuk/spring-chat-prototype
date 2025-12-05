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

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MessageEntityService {
    private final MessageEntityRepository messageEntDao;

    public MessageEntity save(MessageEntity message) {
        return messageEntDao.save(message);
    }

    public List<MessageEntity> findAllMessagesInChatRoom(Long roomId, UserEntity user) {
        return messageEntDao.findMessagesByRoomAndUser(roomId, user);
    }

    public List<MessageEntity> findMessagesByUsersNames(String currentUser, String recipient) {
        return messageEntDao.findMessagesByUsersNames(currentUser, recipient);
    }

    public List<MessageEntity> findMessagesByUsersId(Long currentUser, Long recipient) {
        return messageEntDao.findMessagesByUsersId(currentUser, recipient);
    }
    @jakarta.transaction.Transactional
    public void deleteByTimestamp(Long time) {
        messageEntDao.deleteByTimestamp(new Timestamp(time));
    }

    public void delete(MessageEntity message) {
        messageEntDao.delete(message);
    }

    public MessageEntity findByTimestamp(Long time) {
        return messageEntDao.findByTimestamp(new Timestamp(time));
    }
}