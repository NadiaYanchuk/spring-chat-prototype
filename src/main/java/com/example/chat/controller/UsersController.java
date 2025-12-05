package com.example.chat.controller;

import com.example.chat.dto.UserDTO;
import com.example.chat.entity.UserEntity;
import com.example.chat.service.UserEntityService;
import lombok.AllArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin
@AllArgsConstructor
public class UsersController {

    private final UserEntityService userService;

    @GetMapping("/fetchallusers")
    public List<UserDTO> fetchAll(Model model, Principal principal, @RequestParam String searchTerm) {
        return userService.findUsersWithoutRoomByUser(principal.getName(), searchTerm);
    }

    @GetMapping("/fetchknownusers")
    public List<UserDTO> fetchKnown(Model model, Principal principal) {
        return userService.findAllUsersThatPrincipalKnows(principal.getName());
    }

    @GetMapping("/getprincipal")
    public UserDTO getPrincipal(Model model, Principal principal) {
        UserEntity user = userService.findByUsername(principal.getName());

        return  UserDTO.getUserDtoFromUser(user);
    }

    @GetMapping("/fetchuser")
    public UserDTO getUser(Model model, @RequestParam Long id) {
        return UserDTO.getUserDtoFromUser(userService.findById(id));
    }
}
