package ru.practicum.shareit.comment.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StateBooking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
    @Mock
    private ItemStorage itemStorage;
    @Mock
    private UserStorage userStorage;
    @Mock
    private BookingStorage bookingStorage;
    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    public void shouldThrowValidExceptionWhenUserNotBookedItemAndWantCreatingComment() {
        User user = new User();
        user.setId(1L);
        user.setName("Max");
        user.setEmail("maxiTrue@gmail.ru");
        Item item = new Item();
        item.setId(1L);
        item.setName("сварка");
        item.setDescription("аппарат лучше самого классного");
        item.setOwner(user);
        item.setAvailable(Boolean.TRUE);
        item.setRequest(null);

        List<Booking> bookings = new ArrayList<>();
        Mockito.when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.doReturn(bookings).when(bookingStorage).findAllByItemIdAndBookerIdAndStatusAndEndBeforeOrderByEndDesc(
                anyLong(), anyLong(), any(StateBooking.class), any(LocalDateTime.class));
        ValidException exception = assertThrows(
                ValidException.class,
                () -> commentService.create(new CommentDto(), 1L, 1L)
        );
        assertThat(exception.getMessage()).isEqualTo("Пользователь id - 1 не бронировал вещь id - 1");
    }

}