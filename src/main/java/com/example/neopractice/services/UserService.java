package com.example.neopractice.services;

import com.example.neopractice.exceptions.types.DuplicateResourceException;
import com.example.neopractice.exceptions.types.InvalidCredentialsException;
import com.example.neopractice.exceptions.types.UserNotFoundException;
import com.example.neopractice.models.dtos.requests.LoginRequest;
import com.example.neopractice.models.dtos.requests.RegisterRequest;
import com.example.neopractice.models.dtos.responses.UserResponse;
import com.example.neopractice.models.entities.User;
import com.example.neopractice.models.mappers.AuthMapper;
import com.example.neopractice.models.mappers.UserMapper;
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
    private final UserMapper userMapper;

    public String register(RegisterRequest request) {
        validate(request);
        User user = authMapper.toUserEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return jwtUtil.generateToken(user.getUsername());
    }

    private void validate(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Имя пользователя уже используется");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Почта уже используется");
        }
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Неверный пароль");
        }
        return jwtUtil.generateToken(user.getUsername());
    }

    public UserResponse getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        return userMapper.toUserResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toUserResponseList(users);
    }
}
