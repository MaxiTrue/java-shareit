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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private final BookingMapper bookingMapper;

    @Override
    public ResponseBookingDto create(BookingDto bookingDto, long userId) throws Throwable {
        //получение бронирующего
        User booker = userStorage.findById(userId)
                .orElseThrow((Supplier<Throwable>) () -> new ObjectNotFoundException("пользователь", userId));
        bookingDto.setBookerId(userId);
        //получение вещи бронирвоания
        Item item = itemStorage
                .findById(bookingDto.getItemId())
                .orElseThrow((Supplier<Throwable>) () -> new ObjectNotFoundException("вещь", bookingDto.getItemId()));
        if (!item.getAvailable()) throw new ValidException("Вещь не доступна для бронирования");
        validBookingDto(bookingDto); // валидация данных
        Booking booking = bookingMapper.toEntityBooking(bookingDto, booker, item); //получение сущности из компонентов
        Booking bookingFromStorage = bookingStorage.save(booking); //сохранение в БД
        return bookingMapper.toResponseBookingDto(bookingFromStorage);
    }

    @Override
    public ResponseBookingDto updateStatus(long bookingId, boolean approved, long userId) throws Throwable {
        //получение объекта бронирования
        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow((Supplier<Throwable>) () -> new ObjectNotFoundException("бронирование", bookingId));
        //проверка на владельца вещи
        if (booking.getItem().getOwner().getId() != userId) {
            throw new ObjectNotFoundException("бронирование", bookingId);
        }

        if(booking.getStatus().equals(approved ? StateBooking.APPROVED : StateBooking.REJECTED)){
            throw new ValidException("Свойство available уже находиться в этом состоянии");
        }

        booking.setStatus(approved ? StateBooking.APPROVED : StateBooking.REJECTED); //меняем статус
        //Item item = booking.getItem();
        //item.setAvailable(false);
        Booking bookingFromStorage = bookingStorage.save(booking); //сохранение в БД
        //itemStorage.save(item);
        return bookingMapper.toResponseBookingDto(bookingFromStorage);
    }

    @Override
    public ResponseBookingDto findById(long bookingId, long userId) throws Throwable {
        //получение объекта бронирования
        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow((Supplier<Throwable>) () -> new ObjectNotFoundException("бронирование", bookingId));
        //получаем пользователя который делает запрос
        //User user = userStorage.findById(userId)
                //.orElseThrow((Supplier<Throwable>) () -> new ObjectNotFoundException("пользователь", userId));
        //доступ имеют только владелец вещи и бронирующий
        if (booking.getItem().getOwner().getId() != userId && booking.getBooker().getId() != userId) {
            throw new ObjectNotFoundException("бронирование", bookingId);
        }
        return bookingMapper.toResponseBookingDto(booking);
    }

    @Override
    public Collection<ResponseBookingDto> findAllBookingByBooker(long userId, String state) throws Throwable {
        if (!userStorage.existsById(userId)) throw new ObjectNotFoundException("пользователь", userId);
        List<Booking> bookings = new LinkedList<>();
        switch (state) {
            case "ALL":
                bookings = (List<Booking>) bookingStorage.findAllByBooker_IdOrderByStartDesc(userId);
                break;
            case "WAITING":
                bookings = (List<Booking>) bookingStorage
                        .findAllByStatusAndBookerIdOrderByStartDesc(StateBooking.WAITING, userId);
                break;
            case "REJECTED":
                bookings = (List<Booking>) bookingStorage
                        .findAllByStatusAndBookerIdOrderByStartDesc(StateBooking.REJECTED, userId);
                break;
            case "CURRENT":
                bookings = (List<Booking>) bookingStorage
                        .findAllByStatusAndDateBetweenStartAndEnd(userId, StateBooking.APPROVED, LocalDateTime.now());
            case "PAST":
                bookings = (List<Booking>) bookingStorage
                        .findAllByBookerIdAndStatusAndEndBeforeOrderByStartDesc(
                                userId,
                                StateBooking.APPROVED,
                                LocalDateTime.now());
            case "FUTURE":
                bookings = (List<Booking>) bookingStorage
                        .findAllByBookerIdAndStatusAndStartAfterOrderByStartDesc(
                                userId,
                                StateBooking.APPROVED,
                                LocalDateTime.now());
        }
        return bookings.size() == 0 ? new LinkedList<>() : bookingMapper.toResponseBookingDto(bookings);
    }

    @Override
    public Collection<ResponseBookingDto> findAllBookingForOwnerByAllItems(long userId, String state) throws Throwable {
        if (!userStorage.existsById(userId)) throw new ObjectNotFoundException("пользователь", userId);
        List<Booking> bookings = new LinkedList<>();
        switch (state) {
            case "ALL":
                bookings = (List<Booking>) bookingStorage.findAllBookingByOwnerItems(userId);
                break;
            case "WAITING":
                bookings = (List<Booking>) bookingStorage.findAllBookingByOwnerItemsAndStatus(
                        userId,
                        StateBooking.WAITING);
                break;
            case "REJECTED":
                bookings = (List<Booking>) bookingStorage.findAllBookingByOwnerItemsAndStatus(
                        userId,
                        StateBooking.REJECTED);
                break;
            case "CURRENT":
                bookings = (List<Booking>) bookingStorage
                        .findAllBookingByOwnerItemsAndStatusAndDateBetweenStartAndEnd(
                                userId,
                                StateBooking.APPROVED,
                                LocalDateTime.now());
            case "PAST":
                bookings = (List<Booking>) bookingStorage
                        .findAllBookingByOwnerItemsAndStatusAndEndBefore(
                                userId,
                                StateBooking.APPROVED,
                                LocalDateTime.now());
            case "FUTURE":
                bookings = (List<Booking>) bookingStorage
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

        List<Booking> bookings = (List<Booking>) bookingStorage
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
