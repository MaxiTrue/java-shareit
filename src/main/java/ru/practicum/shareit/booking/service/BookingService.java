package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidException;

import java.util.List;

public interface BookingService {

    ResponseBookingDto create(BookingDto bookingDto, long userId) throws ObjectNotFoundException, ValidException;

    ResponseBookingDto updateStatus(long bookingId, boolean approved, long userId)
            throws ObjectNotFoundException, ValidException;

    ResponseBookingDto findById(long bookingId, long userid) throws ObjectNotFoundException;

    List<ResponseBookingDto> findAllBookingByBooker(long userId, String state, Pageable pageable)
            throws ValidException, ObjectNotFoundException;

    List<ResponseBookingDto> findAllBookingForOwnerByAllItems(long userId, String state, Pageable pageable)
            throws ValidException, ObjectNotFoundException;
}
