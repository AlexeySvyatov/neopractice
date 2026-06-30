package com.example.neopractice.controllers;

import com.example.neopractice.models.entities.User;
import com.example.neopractice.models.mappers.UserMapper;
import com.example.neopractice.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        try {
            String username = (String) request.getAttribute("username");
            User user = userService.getCurrentUser(username);
            return ResponseEntity.ok(userMapper.toUserResponse(user));
        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(userMapper.toUserResponseList(users));
        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }
}
