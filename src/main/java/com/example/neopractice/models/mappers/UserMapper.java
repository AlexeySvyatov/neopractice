package com.example.neopractice.models.mappers;

import com.example.neopractice.models.dtos.UserResponse;
import com.example.neopractice.models.entities.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getCreatedAt());
    }

    public List<UserResponse> toUserResponseList(List<User> users) {
        return users.stream().map(this::toUserResponse).collect(Collectors.toList());
    }
}
