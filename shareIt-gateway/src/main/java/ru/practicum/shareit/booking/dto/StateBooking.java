package ru.practicum.shareit.booking.dto;

import java.util.Optional;

public enum StateBooking {
    ALL,      // все
    CURRENT,  // текущие
    FUTURE,   // будущие
    PAST,     // прошедшие
    WAITING,  // ожидающие
    APPROVED, // подтвержденные
    REJECTED; // отклонённые

    public static Optional<StateBooking> from(String stringState) {
        for (StateBooking state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}

