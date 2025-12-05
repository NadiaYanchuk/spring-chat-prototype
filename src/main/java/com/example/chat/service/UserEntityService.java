package com.example.chat.service;

import com.example.chat.dto.UserDTO;
import com.example.chat.entity.UserEntity;
import com.example.chat.repository.UserEntityRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UserEntityService {

    private final UserEntityRepository userDao;

    private final PasswordEncoder passwordEncoder;

    public void addUser(final UserEntity user) {
        log.info("Adding new user: {}", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.save(user);
        log.info("User successfully added: {}", user.getUsername());
    }

    public List<UserDTO> findAllWithoutPrincipal(String username) {
        return userDao.findAllByUsernameNot(username).stream().map(UserDTO::getUserDtoFromUser).toList();
    }

    public List<UserDTO> findAllUsersThatPrincipalKnows(String username) {
        List<Long> ids = userDao.findConversationParticipantIdsByUsername(username);
        return userDao.findAllById(ids).stream().map(UserDTO::getUserDtoFromUser).toList();
    }

    public UserEntity findByUsername(String username) {
        UserEntity user = userDao.findByUsername(username);
        if (user == null) {
            log.error("User not found with username: {}", username);
            throw new IllegalArgumentException("User not found with username: " + username);
        }
        return user;
    }

    public UserEntity findById(Long id) {
        return userDao.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", id);
                    return new IllegalArgumentException("User not found with id: " + id);
                });
    }

    public List<UserDTO> findUsersWithoutRoomByUser(String name, String searchTerm) {
        UserEntity user = userDao.findByUsername(name);

        return userDao.findUsersWithoutRoomByUser(user, user.getId(), searchTerm).stream().map(UserDTO::getUserDtoFromUser).toList();
    }

    public boolean ifExistByUserName(String username) {
        return userDao.existsByUsername(username);
    }

    public boolean ifExistById(Long id) {
        return userDao.existsById(id);
    }
}