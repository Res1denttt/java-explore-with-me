package ru.practicum.ewm_service.event.validation;

import org.springframework.stereotype.Component;
import ru.practicum.ewm_service.event.model.EventState;
import ru.practicum.ewm_service.exception.ConditionsNotMetException;

@Component
public class UpdateEventUserRequestValidator extends UpdateEventValidator {

    protected UpdateEventUserRequestValidator() {
        super(2);
    }

    @Override
    protected void validateEventState(EventState state) {
        if (state.equals(EventState.PUBLISHED))
            throw new ConditionsNotMetException("Нельзя изменить опубликованное событие");
    }
}
