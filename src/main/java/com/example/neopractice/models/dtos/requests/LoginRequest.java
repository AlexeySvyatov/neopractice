package com.example.neopractice.models.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
        @NotBlank(message = "Имя пользователя обязательно")
        @Size(max = 25)
        private String username;
        @NotBlank(message = "Пароль обязателен")
        @Size(min = 6, max = 20, message = "Пароль от 6 до 20 символов")
        private String password;
}
