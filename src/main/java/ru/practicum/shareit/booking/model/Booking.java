package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDate;

@Data
@Builder
public class Booking {
    private long id;
    private LocalDate start;
    private LocalDate end;
    private Item item;
    private String status; //TODO пока строка, потом посмотрю как сделать
}
