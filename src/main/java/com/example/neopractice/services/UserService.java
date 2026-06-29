package com.example.neopractice.services;

import com.example.neopractice.models.dtos.LoginRequest;
import com.example.neopractice.models.dtos.RegisterRequest;
import com.example.neopractice.models.entities.User;
import com.example.neopractice.models.mappers.AuthMapper;
import com.example.neopractice.util.JwtUtil;
import com.example.neopractice.util.PasswordEncoder;
import com.example.neopractice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthMapper authMapper;

    public String register(RegisterRequest request) {
        validate(request);

        User user = authMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        return jwtUtil.generateToken(user.getUsername());
    }

    private void validate(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Имя пользователя уже используется");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Почта уже используется");
        }
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new RuntimeException("Неверный пароль");
        }

        return jwtUtil.generateToken(user.getUsername());
    }

    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
