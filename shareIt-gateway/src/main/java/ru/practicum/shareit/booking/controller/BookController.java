package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.booking.client.BookClient;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.StateBooking;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookController {

    private final BookClient bookClient;

    @PostMapping
    public ResponseEntity<Object> create(@Validated({Create.class}) @RequestBody BookingDto bookingDto,
                                         @RequestHeader("X-Sharer-User-Id") @NotNull long userId) {
        log.debug("Получен запрос POST на создание бронирования вещи - {}", bookingDto.getItemId());
        return bookClient.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateStatus(@PathVariable("bookingId") long bookingId,
                                               @RequestParam("approved") Boolean approved,
                                               @RequestHeader("X-Sharer-User-Id") @NotNull long userId) {
        log.debug("Получен запрос PATCH на смену статуса бронирования - {}", bookingId);
        return bookClient.updateStatus(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(@PathVariable("bookingId") long bookingId,
                                           @RequestHeader("X-Sharer-User-Id") @NotNull long userId) {
        log.debug("Получен запрос GET на получение бронирования - {}, пользователем - {}", bookingId, userId);
        return bookClient.findById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllBookingByBooker(
            @RequestParam(value = "state", defaultValue = "ALL", required = false) String stringState,
            @RequestHeader("X-Sharer-User-Id") @NotNull long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
            @Positive @RequestParam(name = "size", defaultValue = "5") int size) {
        log.debug("Получен запрос GET на получение всех бронирований пользователя - {} с статусом - {}",
                userId, stringState);
        String stateResult = stringState.toUpperCase().replaceAll("\\s", "");
        StateBooking state = StateBooking.from(stateResult)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stringState));
        return bookClient.findAllBookingByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllBookingForOwnerByAllItems(
            @RequestParam(value = "state", defaultValue = "ALL", required = false) String stringState,
            @RequestHeader("X-Sharer-User-Id") @NotNull long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
            @Positive @RequestParam(name = "size", defaultValue = "5") int size) {
        log.debug("Получен запрос GET на получение всех бронирований пользователя - {}, по всем вещам с статусом - {}",
                userId, stringState);
        String stateResult = stringState.toUpperCase().replaceAll("\\s", "");
        StateBooking state = StateBooking.from(stateResult)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stringState));
        return bookClient.findAllBookingForOwnerByAllItems("/owner", userId, state, from, size);
    }
}
