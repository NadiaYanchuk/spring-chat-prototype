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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class DataLoader {

    @Bean
    public UserEntity user1() {
        UserEntity user = new UserEntity();
        user.setUsername("n1kry");
        user.setEmail("1@mail.com");
        user.setPassword("1");

        return user;
    }

    @Bean
    public UserEntity user2() {
        UserEntity user = new UserEntity();
        user.setUsername("n1kry2");
        user.setEmail("2@mail.com");
        user.setPassword("1");

        return user;
    }

    @Bean
    public UserEntity user3() {
        UserEntity user = new UserEntity();
        user.setUsername("n1kry3");
        user.setEmail("3@mail.com");
        user.setPassword("1");

        return user;
    }

    @Bean
    public UserEntity friend1() {
        UserEntity user = new UserEntity();
        user.setUsername("friend1");
        user.setEmail("4@mail.com");
        user.setPassword("1");

        return user;
    }

    @Bean
    public UserEntity friend2() {
        UserEntity user = new UserEntity();
        user.setUsername("friend2");
        user.setEmail("5@mail.com");
        user.setPassword("1");

        return user;
    }

    @Bean
    public RoomEntity room1() {
        RoomEntity room = new RoomEntity();

        return room;
    }

    @Bean
    public RoomEntity room2() {
        RoomEntity room = new RoomEntity();

        return room;
    }

    @Bean
    public MessageEntity message1() {
        MessageEntity message = new MessageEntity();

        return message;
    }

    @Bean
    public MessageEntity message2() {
        MessageEntity message = new MessageEntity();

        return message;
    }
}