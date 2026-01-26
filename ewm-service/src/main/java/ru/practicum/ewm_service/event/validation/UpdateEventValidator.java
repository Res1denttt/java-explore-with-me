package ru.practicum.ewm_service.event.validation;

import ru.practicum.ewm_service.event.dto.UpdateRequest;
import ru.practicum.ewm_service.event.model.EventState;
import ru.practicum.ewm_service.exception.ValidationException;

import java.time.LocalDateTime;

public abstract class UpdateEventValidator {

    protected final int permittedHoursToEvent;

    protected UpdateEventValidator(int permittedHoursToEvent) {
        this.permittedHoursToEvent = permittedHoursToEvent;
    }

    public void validate(UpdateRequest request, EventState state, LocalDateTime oldEventDate) {
        validateEventDate(oldEventDate, request.getEventDate());
        validateEventState(state);
    }

    protected void validateEventDate(LocalDateTime oldEventDate, LocalDateTime newEventDate) {
        checkDate(oldEventDate);
        if (newEventDate == null) return;
        checkDate(newEventDate);
    }

    protected abstract void validateEventState(EventState state);

    private void checkDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(permittedHoursToEvent)))
            throw new ValidationException("минимальное количество часов до начала события = " + permittedHoursToEvent);
    }
}
