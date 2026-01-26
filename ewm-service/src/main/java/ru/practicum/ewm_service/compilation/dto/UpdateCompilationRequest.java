package ru.practicum.ewm_service.compilation.dto;

import jakarta.validation.constraints.Size;

import java.util.Set;

public record UpdateCompilationRequest(
        Set<Long> events,

        Boolean pinned,

        @Size(max = 50, message = "Длинна заголовка должна быть не более 50 символов")
        String title) {
}
