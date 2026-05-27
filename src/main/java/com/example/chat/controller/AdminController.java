package com.example.chat.controller;

import com.example.chat.dto.UserDTO;
import com.example.chat.service.UserEntityService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@AllArgsConstructor
public class AdminController {

    private final UserEntityService userService;

    @GetMapping("/admin")
    public String adminPage() {
        return "admin";
    }

    @GetMapping("/admin/users")
    @ResponseBody
    public List<UserDTO> getAllUsers() {
        return userService.findAllUsers();
    }

    @DeleteMapping("/admin/users/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.softDeleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/admin/users/{id}/restore")
    @ResponseBody
    public ResponseEntity<Void> restoreUser(@PathVariable Long id) {
        userService.restoreUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/admin/users/{id}/role")
    @ResponseBody
    public ResponseEntity<Void> changeRole(@PathVariable Long id, @RequestParam String role) {
        userService.changeRole(id, role);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/admin/users/{id}/ban")
    @ResponseBody
    public ResponseEntity<Void> banUser(@PathVariable Long id, @RequestParam(defaultValue = "24") int hours) {
        userService.banUser(id, hours);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/admin/users/{id}/unban")
    @ResponseBody
    public ResponseEntity<Void> unbanUser(@PathVariable Long id) {
        userService.unbanUser(id);
        return ResponseEntity.noContent().build();
    }
}