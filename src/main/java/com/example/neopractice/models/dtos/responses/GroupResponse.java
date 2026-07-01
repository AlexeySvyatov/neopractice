package com.example.neopractice.models.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupResponse {
    private UUID id;
    private String name;
    private String description;
    private UserResponse owner;
    private LocalDateTime createdAt;
}
