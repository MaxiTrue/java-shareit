package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StateBooking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private final BookingMapper bookingMapper;

    @Override
    public ResponseBookingDto create(BookingDto bookingDto, long userId)
            throws ObjectNotFoundException, ValidException {
        //получение бронирующего
        User booker = userStorage.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("пользователь", userId));
        bookingDto.setBookerId(userId);
        //получение вещи бронирвоания
        Item item = itemStorage
                .findById(bookingDto.getItemId())
                .orElseThrow(() -> new ObjectNotFoundException("вещь", bookingDto.getItemId()));
        if (!item.getAvailable()) throw new ValidException("Вещь не доступна для бронирования");
        if (item.getOwner().getId() == userId) throw new ObjectNotFoundException("вещь", item.getId());
        validBookingDto(bookingDto); // валидация данных
        Booking booking = bookingMapper.toEntityBooking(bookingDto, booker, item); //получение сущности из компонентов
        Booking bookingFromStorage = bookingStorage.save(booking); //сохранение в БД
        return bookingMapper.toResponseBookingDto(bookingFromStorage);
    }

    @Override
    public ResponseBookingDto updateStatus(long bookingId, boolean approved, long userId)
            throws ObjectNotFoundException, ValidException {
        //получение объекта бронирования
        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("бронирование", bookingId));
        //проверка на владельца вещи
        if (booking.getItem().getOwner().getId() != userId) {
            throw new ObjectNotFoundException("бронирование", bookingId);
        }

        if (booking.getStatus().equals(approved ? StateBooking.APPROVED : StateBooking.REJECTED)) {
            throw new ValidException("Свойство available уже находиться в этом состоянии");
        }

        booking.setStatus(approved ? StateBooking.APPROVED : StateBooking.REJECTED); //меняем статус
        Booking bookingFromStorage = bookingStorage.save(booking); //сохранение в БД
        return bookingMapper.toResponseBookingDto(bookingFromStorage);
    }

    @Override
    public ResponseBookingDto findById(long bookingId, long userId) throws ObjectNotFoundException {
        //получение объекта бронирования
        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("бронирование", bookingId));
        //доступ имеют только владелец вещи и бронирующий
        if (booking.getItem().getOwner().getId() != userId && booking.getBooker().getId() != userId) {
            throw new ObjectNotFoundException("бронирование", bookingId);
        }
        return bookingMapper.toResponseBookingDto(booking);
    }

    @Override
    public List<ResponseBookingDto> findAllBookingByBooker(long userId, String state)
            throws ObjectNotFoundException {
        if (!userStorage.existsById(userId)) throw new ObjectNotFoundException("пользователь", userId);
        List<Booking> bookings = new LinkedList<>();
        switch (state) {
            case "ALL":
                bookings = bookingStorage.findAllByBooker_IdOrderByStartDesc(userId);
                break;
            case "WAITING":
                bookings = bookingStorage
                        .findAllByStatusAndBookerIdOrderByStartDesc(StateBooking.WAITING, userId);
                break;
            case "REJECTED":
                bookings = bookingStorage
                        .findAllByStatusAndBookerIdOrderByStartDesc(StateBooking.REJECTED, userId);
                break;
            case "CURRENT":
                bookings = bookingStorage
                        .findAllByStatusAndDateBetweenStartAndEnd(userId, StateBooking.APPROVED, LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingStorage
                        .findAllByBookerIdAndStatusAndEndBeforeOrderByStartDesc(
                                userId,
                                StateBooking.APPROVED,
                                LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingStorage
                        .findAllByBookerIdAndStatusAndStartAfterOrderByStartDesc(
                                userId,
                                StateBooking.APPROVED,
                                LocalDateTime.now());
        }
        return bookings.size() == 0 ? new LinkedList<>() : bookingMapper.toResponseBookingDto(bookings);
    }

    @Override
    public List<ResponseBookingDto> findAllBookingForOwnerByAllItems(long userId, String state)
            throws ObjectNotFoundException {
        if (!userStorage.existsById(userId)) throw new ObjectNotFoundException("пользователь", userId);
        List<Booking> bookings = new LinkedList<>();
        switch (state) {
            case "ALL":
                bookings = bookingStorage.findAllBookingByOwnerItems(userId);
                break;
            case "WAITING":
                bookings = bookingStorage.findAllBookingByOwnerItemsAndStatus(
                        userId,
                        StateBooking.WAITING);
                break;
            case "REJECTED":
                bookings = bookingStorage.findAllBookingByOwnerItemsAndStatus(
                        userId,
                        StateBooking.REJECTED);
                break;
            case "CURRENT":
                bookings = bookingStorage
                        .findAllBookingByOwnerItemsAndStatusAndDateBetweenStartAndEnd(
                                userId,
                                StateBooking.APPROVED,
                                LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingStorage
                        .findAllBookingByOwnerItemsAndStatusAndEndBefore(
                                userId,
                                StateBooking.APPROVED,
                                LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingStorage
                        .findAllBookingByOwnerItemsAndStatusAndStartAfter(
                                userId,
                                StateBooking.APPROVED,
                                LocalDateTime.now());
        }

        return bookings.size() == 0 ? new LinkedList<>() : bookingMapper.toResponseBookingDto(bookings);
    }

    private void validBookingDto(BookingDto bookingDto) throws ValidException {

        LocalDateTime bookingDtoStart = bookingDto.getStart();
        LocalDateTime bookingDtoEnd = bookingDto.getEnd();

        if (bookingDtoStart.isBefore(LocalDateTime.now())) {
            throw new ValidException("Старт бронирования не может быть в прошлом");
        }

        if (bookingDtoStart.isAfter(bookingDtoEnd)) {
            throw new ValidException("Старт бронирования не может быть раньше окончания");
        }

        List<Booking> bookings = bookingStorage
                .findAllByItemIdAndStatusAndEndAfterOrderByStartAsc(
                        bookingDto.getItemId(),
                        StateBooking.APPROVED,
                        bookingDtoStart);

        for (Booking booking : bookings) {
            if (booking.getStart().isBefore(bookingDtoEnd) && booking.getEnd().isAfter(bookingDtoStart)) {
                throw new ValidException("Даты для бронирования заняты!");
            }
        }
    }

}
