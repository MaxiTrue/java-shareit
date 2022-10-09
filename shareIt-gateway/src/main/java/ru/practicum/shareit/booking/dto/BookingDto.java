package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.Create;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id;
    @NotNull
    @FutureOrPresent(groups = {Create.class})
    private LocalDateTime start;
    @NotNull
    @FutureOrPresent(groups = {Create.class})
    private LocalDateTime end;
    private Long itemId;
    private Long bookerId;
    private StateBooking status = StateBooking.WAITING;
}
