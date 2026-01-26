package ru.practicum.ewm_service.event.validation;

import org.springframework.stereotype.Component;
import ru.practicum.ewm_service.event.model.EventState;
import ru.practicum.ewm_service.exception.ConditionsNotMetException;

@Component
public class UpdateEventAdminRequestValidator extends UpdateEventValidator {

    protected UpdateEventAdminRequestValidator() {
        super(1);
    }

    @Override
    protected void validateEventState(EventState state) {
        if (!state.equals(EventState.PENDING))
            throw new ConditionsNotMetException("Невозможно изменить событие. Статус должен быть: PENDING");
    }
}
