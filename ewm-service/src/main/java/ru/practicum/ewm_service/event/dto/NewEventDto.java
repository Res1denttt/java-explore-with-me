package ru.practicum.ewm_service.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import ru.practicum.ewm_service.event.model.Location;
import ru.practicum.ewm_service.exception.ValidationException;

import java.time.LocalDateTime;

public record NewEventDto(
        @NotBlank
        @Size(min = 20, max = 2000, message = "длинна аннотации должна быть от 20 до 2000 символов")
        String annotation,

        @NotBlank
        @Size(min = 20, max = 7000, message = "длинна аннотации должна быть от 20 до 7000 символов")
        String description,

        @NotNull
        LocalDateTime eventDate,

        boolean paid,

        @PositiveOrZero
        int participantLimit,

        Boolean requestModeration,

        @NotBlank
        @Size(min = 3, max = 120, message = "длинна заголовка должна быть от 3 до 120 символов")
        String title,

        long category,

        @NotNull
        Location location) {

    public NewEventDto {
        if (requestModeration == null) {
            requestModeration = true;
        }
        if (eventDate == null || eventDate.isBefore(LocalDateTime.now().plusHours(2)))
            throw new ValidationException("начало события не может быть раньше, чем через 2 часа");
    }
}
