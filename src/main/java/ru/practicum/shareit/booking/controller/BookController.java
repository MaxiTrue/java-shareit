package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidException;

import javax.validation.constraints.NotNull;
import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseBookingDto create(@RequestBody BookingDto bookingDto,
                                     @RequestHeader("X-Sharer-User-Id") @NotNull long userid)
            throws ObjectNotFoundException, ValidException {
        log.debug("Получен запрос POST на создание бронирования вещи - {}", bookingDto.getItemId());
        return bookingService.create(bookingDto, userid);
    }

    @PatchMapping("/{bookingId}")
    public ResponseBookingDto updateStatus(@PathVariable("bookingId") long bookingId,
                                           @RequestParam("approved") Boolean approved,
                                           @RequestHeader("X-Sharer-User-Id") @NotNull long userId)
            throws ObjectNotFoundException, ValidException {
        log.debug("Получен запрос PATCH на смену статуса бронирования - {}", bookingId);
        return bookingService.updateStatus(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseBookingDto findById(@PathVariable("bookingId") long bookingId,
                                       @RequestHeader("X-Sharer-User-Id") @NotNull long userId)
            throws ObjectNotFoundException {
        log.debug("Получен запрос GET на получение бронирования - {}, пользователем - {}", bookingId, userId);
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping
    public Collection<ResponseBookingDto> findAllBookingByBooker(
            @RequestParam(value = "state", defaultValue = "ALL", required = false) String state,
            @RequestHeader("X-Sharer-User-Id") @NotNull long userId) throws ValidException {
        log.debug("Получен запрос GET на получение всех бронирований пользователя - {} с статусом - {}", userId, state);
        String stateResult = state.toUpperCase().replaceAll("\\s", "");
        return bookingService.findAllBookingByBooker(userId, stateResult);
    }

    @GetMapping("/owner")
    public Collection<ResponseBookingDto> findAllBookingForOwnerByAllItems(
            @RequestParam(value = "state", defaultValue = "ALL", required = false) String state,
            @RequestHeader("X-Sharer-User-Id") @NotNull long userId) throws ValidException {
        log.debug("Получен запрос GET на получение всех бронирований пользователя - {}, по всем вещам с статусом - {}",
                userId, state);
        String stateResult = state.toUpperCase().replaceAll("\\s", "");
        return bookingService.findAllBookingForOwnerByAllItems(userId, stateResult);
    }
}
