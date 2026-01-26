package ru.practicum.ewm_service.event.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import ru.practicum.ewm_service.event.model.Location;

import java.time.LocalDateTime;

public record UpdateEventUserRequest(
        @Size(min = 20, max = 2000, message = "длинна аннотации должна быть от 20 до 2000 символов")
        String annotation,

        @Size(min = 20, max = 7000, message = "длинна аннотации должна быть от 20 до 7000 символов")
        String description,

        LocalDateTime eventDate,

        Boolean paid,

        @PositiveOrZero
        Integer participantLimit,

        Boolean requestModeration,

        StateAction stateAction,

        @Size(min = 3, max = 120, message = "длинна заголовка должна быть от 3 до 120 символов")
        String title,

        Long category,

        Location location) implements UpdateRequest {

    @Override
    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public enum StateAction {
        SEND_TO_REVIEW,
        CANCEL_REVIEW
    }
}
