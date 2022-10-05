package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private UserStorage userStorage;
    @Mock
    private ItemStorage itemStorage;
    @Mock
    private BookingStorage bookingStorage;
    @Mock
    private BookingMapper bookingMapper;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private Item item;
    private Booking booking;

    @BeforeEach
    public void beforeEach() {
        user = new User();
        user.setId(1L);
        user.setName("Max");
        user.setEmail("maxiTrue@gmail.ru");
        item = new Item();
        item.setId(1L);
        item.setName("сварка");
        item.setDescription("аппарат лучше самого классного");
        item.setOwner(user);
        item.setAvailable(Boolean.TRUE);
        item.setRequest(null);
        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(StateBooking.APPROVED);
    }

    //должен бросить исключение при создании бронирования, когда пользователь бронирующего не существует
    @Test
    public void shouldThrowObjectNotFoundExceptionWhenCreatingNewBookingWithNotExistBooker() {
        Mockito.when(userStorage.findById(anyLong())).thenReturn(Optional.empty());
        ObjectNotFoundException exception = assertThrows(
                ObjectNotFoundException.class,
                () -> bookingService.create(new BookingDto(), 1L));
        assertThat(exception.getNameObject()).isEqualTo("пользователь");
        assertThat(exception.getIdObject()).isEqualTo(1L);
    }

    //должен бросить исключение при создании бронирования когда у вещи статус доступности в значении false
    @Test
    public void shouldThrowValidExceptionWhenCreatingNewBookingWhenAvailableFalse() {
        item.setAvailable(Boolean.FALSE);
        Mockito.when(userStorage.findById(anyLong())).thenReturn(Optional.of(new User()));
        Mockito.when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        ValidException exception = assertThrows(
                ValidException.class,
                () -> bookingService.create(BookingDto.builder().itemId(1L).build(), 1L));
        assertThat(exception.getMessage()).isEqualTo("Вещь не доступна для бронирования");
    }

    //должен бросить исключение при создании бронирования когда владелец вещи равен бронирующему
    @Test
    public void shouldThrowObjectNotFoundExceptionWhenCreatingNewBookingWhenBookerEqualsOwner() {
        Mockito.when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        ObjectNotFoundException exception = assertThrows(
                ObjectNotFoundException.class,
                () -> bookingService.create(BookingDto.builder().itemId(1L).build(), 1L));
        assertThat(exception.getNameObject()).isEqualTo("Нельзя бронировать свою вещь");
    }

    //должен бросить исключение когда пользователь пытающийся обновить статус не является владельцем вещи
    @Test
    public void shouldThrowObjectNotFoundExceptionWhenUpdatingStateAndOwnerNotEqualsRequester() {
        Mockito.when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));
        ObjectNotFoundException exception = assertThrows(
                ObjectNotFoundException.class,
                () -> bookingService.updateStatus(1L, Boolean.TRUE, 100L));
        assertThat(exception.getNameObject()).isEqualTo("бронирование");
    }

    //должен бросить статус когда пытаемся обновить статус на тот который уже установлен
    @Test
    public void shouldThrowValidExceptionWhenUpdatingStateToTheSame() {
        booking.setStatus(StateBooking.APPROVED);
        Mockito.when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));
        ValidException exception = assertThrows(
                ValidException.class,
                () -> bookingService.updateStatus(1L, Boolean.TRUE, 1L));
        assertThat(exception.getMessage()).isEqualTo("Свойство available уже находиться в этом состоянии");
    }

    //должен вызвать 1 раз метод для получения всех бронирвоаний по стутсу ALL
    @Test
    public void checkReceivingBookingsByStatus_All() throws ObjectNotFoundException, ValidException {
        Page<Booking> page = new PageImpl<>(List.of(booking));
        Mockito.when(userStorage.existsById(anyLong())).thenReturn(Boolean.TRUE);
        Mockito.when(bookingStorage.findAllByBookerIdOrderByStartDesc(anyLong(), any())).thenReturn(page);
        Mockito.when(bookingMapper.toResponseBookingDto(anyList())).thenReturn(List.of(new ResponseBookingDto()));
        bookingService.findAllBookingByBooker(1L, "ALL", PageRequest.of(1, 1));
        Mockito
                .verify(bookingStorage, Mockito.atMost(1))
                .findAllByBookerIdOrderByStartDesc(anyLong(), any());
        Mockito.verify(bookingStorage, Mockito.never())
                .findAllByStatusAndBookerIdOrderByStartDesc(any(), anyLong(), any());
    }

    //должен вызвать 1 раз метод для получения всех бронирвоаний по статусу WAITING
    @Test
    public void checkReceivingBookingsByStatus_WAITING() throws ObjectNotFoundException, ValidException {
        Page<Booking> page = new PageImpl<>(List.of(booking));
        Mockito.when(userStorage.existsById(anyLong())).thenReturn(Boolean.TRUE);
        Mockito
                .when(bookingStorage.findAllByStatusAndBookerIdOrderByStartDesc(
                        any(StateBooking.class),
                        anyLong(),
                        any())).thenReturn(page);
        Mockito.when(bookingMapper.toResponseBookingDto(anyList())).thenReturn(List.of(new ResponseBookingDto()));
        bookingService.findAllBookingByBooker(1L, "WAITING", PageRequest.of(1, 1));
        Mockito
                .verify(bookingStorage, Mockito.atMost(1))
                .findAllByStatusAndBookerIdOrderByStartDesc(
                        StateBooking.WAITING,
                        1L,
                        PageRequest.of(1, 1));
        Mockito.verify(bookingStorage, Mockito.never())
                .findAllByBookerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    public void checkReceivingBookingsByStatus_REJECTED() throws ObjectNotFoundException, ValidException {
        Page<Booking> page = new PageImpl<>(List.of(booking));
        Mockito.when(userStorage.existsById(anyLong())).thenReturn(Boolean.TRUE);
        Mockito
                .when(bookingStorage.findAllByStatusAndBookerIdOrderByStartDesc(
                        any(StateBooking.class),
                        anyLong(),
                        any())).thenReturn(page);
        Mockito.when(bookingMapper.toResponseBookingDto(anyList())).thenReturn(List.of(new ResponseBookingDto()));
        bookingService.findAllBookingByBooker(1L, "REJECTED", PageRequest.of(1, 1));
        Mockito
                .verify(bookingStorage, Mockito.atMost(1))
                .findAllByStatusAndBookerIdOrderByStartDesc(
                        StateBooking.REJECTED,
                        1L,
                        PageRequest.of(1, 1));
        Mockito.verify(bookingStorage, Mockito.never())
                .findAllByBookerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    public void checkReceivingBookingsByNotExistStatusShouldThrowValidException() {
        Mockito.when(userStorage.existsById(anyLong())).thenReturn(Boolean.TRUE);
        ValidException exception = assertThrows(
                ValidException.class,
                () -> bookingService.findAllBookingByBooker(1L, "TEST", PageRequest.of(1, 1)));
        assertThat(exception.getMessage()).isEqualTo("Unknown state: TEST");
    }
}