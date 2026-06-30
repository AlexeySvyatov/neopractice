package com.example.neopractice.models.mappers;

import com.example.neopractice.models.dtos.RegisterRequest;
import com.example.neopractice.models.entities.User;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {
    public User toEntity(RegisterRequest request) {
        return User.builder().username(request.username()).email(request.email()).passwordHash(null).createdAt(null).build();
    }
}
