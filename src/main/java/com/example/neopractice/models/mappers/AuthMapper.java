package com.example.neopractice.models.mappers;

import com.example.neopractice.models.dtos.requests.RegisterRequest;
import com.example.neopractice.models.entities.User;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {
    public User toUserEntity(RegisterRequest request) {
        return User.builder().
                username(request.getUsername()).
                email(request.getEmail()).
                build();
    }
}
