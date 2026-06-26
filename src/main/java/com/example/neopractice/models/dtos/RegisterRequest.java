package com.example.neopractice.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank(message = "Имя пользователя обязательно")
        String username,
        @NotBlank(message = "Почта обязательна")
        @Email(message = "Неверный формат почты")
        String email,
        @NotBlank(message = "Пароль обязателен")
        String password) {
}
