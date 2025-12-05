package com.example.chat.controller;

import com.example.chat.entity.UserEntity;
import com.example.chat.service.UserEntityService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@AllArgsConstructor
public class AuthController {

    private final UserEntityService userService;

    @GetMapping("/registration")
    public String registerForm(Model model, UserEntity userEntity) {
        model.addAttribute("user", userEntity);
        return "registration";
    }

    @PostMapping("/registration")
    public String registerSubmit(@Valid UserEntity userEntity, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "registration";
        }
        userService.addUser(userEntity);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/chat/logout")
    public String logout() {
        return "redirect:/logout";
    }
}
