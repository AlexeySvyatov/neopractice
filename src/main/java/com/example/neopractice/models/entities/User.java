package com.example.neopractice.models.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Builder.Default
    private UUID id = null;

    private String username;

    private String email;

    private String passwordHash;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
