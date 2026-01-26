package ru.practicum.ewm_service.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewUserRequest(
        @Email
        @NotBlank
        @Size(min = 6, max = 254, message = "Длинна email должна быть от 6 до 254")
        String email,

        @NotBlank
        @Size(min = 2, max = 250, message = "Длинна имени должна быть от 2 до 250")
        String name) {
}