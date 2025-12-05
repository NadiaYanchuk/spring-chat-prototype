package com.example.chat.component;

import com.example.chat.entity.MessageEntity;
import com.example.chat.entity.RoomEntity;
import com.example.chat.entity.UserEntity;
import com.example.chat.service.MessageEntityService;
import com.example.chat.service.RoomEntityService;
import com.example.chat.service.UserEntityService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class DatabaseInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final UserEntity user1;
    private final UserEntity user2;
    private final UserEntity user3;
    private final UserEntity friend1;
    private final UserEntity friend2;

    private final RoomEntity room1;
    private final RoomEntity room2;

    private final MessageEntity message1;
    private final MessageEntity message2;

    private final UserEntityService service;
    private final RoomEntityService roomService;
    private final MessageEntityService messageEntService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        service.addUser(user1);
        service.addUser(user2);
        service.addUser(user3);
        service.addUser(friend1);
        service.addUser(friend2);

        room1.setUser1(user1);
        room1.setUser2(user2);

        roomService.save(room1);

        room2.setUser1(user1);
        room2.setUser2(user3);

        roomService.save(room2);

        message1.setRoom(room1);
        message1.setUser(user1);
        message1.setText("Hello1");
        message1.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));

        messageEntService.save(message1);

        message2.setRoom(room1);
        message2.setUser(user2);
        message2.setText("Hello2");
        message2.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));

        messageEntService.save(message2);
    }
}
