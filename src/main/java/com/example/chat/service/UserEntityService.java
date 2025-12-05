package com.example.chat.service;

import com.example.chat.dto.UserDTO;
import com.example.chat.entity.UserEntity;
import com.example.chat.repository.UserEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserEntityService {

    private final UserEntityRepository userDao;

    private final PasswordEncoder passwordEncoder;

    public void addUser(final UserEntity user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.save(user);
    }

    public List<UserDTO> findAllWithoutPrincipal(String username) {
        return userDao.findAllByUsernameNot(username).stream().map(UserDTO::getUserDtoFromUser).toList();
    }

    public List<UserDTO> findAllUsersThatPrincipalKnows(String username) {
        List<Long> ids = userDao.findConversationParticipantIdsByUsername(username);
        return userDao.findAllById(ids).stream().map(UserDTO::getUserDtoFromUser).toList();
    }

    public UserEntity findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    public UserEntity findById(Long id) {
        return userDao.findById(id).orElse(null);
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