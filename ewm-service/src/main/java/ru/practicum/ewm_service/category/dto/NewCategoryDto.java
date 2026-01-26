package ru.practicum.ewm_service.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewCategoryDto(
        @NotBlank
        @Size(max = 50, message = "Название должно быть не более 50 символов")
        String name) {
}
