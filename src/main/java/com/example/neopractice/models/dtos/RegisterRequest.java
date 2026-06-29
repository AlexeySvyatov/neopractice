package com.example.neopractice.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Имя пользователя обязательно")
        @Size(max = 25)
        String username,
        @NotBlank(message = "Почта обязательна")
        @Email(message = "Неверный формат почты")
        @Size(max = 50)
        String email,
        @NotBlank(message = "Пароль обязателен")
        @Size(min = 6, max = 20, message = "Пароль от 6 до 20 символов")
        String password) {
}
