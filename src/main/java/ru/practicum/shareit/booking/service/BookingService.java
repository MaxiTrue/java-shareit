package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;

import java.util.Collection;

public interface BookingService {

    ResponseBookingDto create(BookingDto bookingDto, long userId) throws Throwable;

    ResponseBookingDto updateStatus(long bookingId, boolean approved, long userId) throws Throwable;

    ResponseBookingDto findById(long bookingId, long userid) throws Throwable;

    Collection<ResponseBookingDto> findAllBookingByBooker(long userId, String state) throws Throwable;

    Collection<ResponseBookingDto> findAllBookingForOwnerByAllItems(long userId, String state) throws Throwable;
}
