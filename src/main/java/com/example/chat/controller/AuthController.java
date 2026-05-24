package com.example.chat.controller;

import com.example.chat.entity.UserEntity;
import com.example.chat.security.JwtService;
import com.example.chat.service.UserEntityService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import jakarta.servlet.http.Cookie;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;


@Controller
@AllArgsConstructor
public class AuthController {

    private final UserEntityService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

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

    @PostMapping("/login")
    public String loginSubmit(@RequestParam String username,
                              @RequestParam String password,
                              HttpServletResponse response,
                              Model model) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            String token = jwtService.generateToken(username);

            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(86400);
            response.addCookie(cookie);

            return "redirect:/chat";
        } catch (BadCredentialsException e) {
            model.addAttribute("error", "Incorrect username or password");
            return "login";
        }
    }

    @GetMapping("/chat/logout")
    public String logout() {
        return "redirect:/logout";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/chat";
    }
}