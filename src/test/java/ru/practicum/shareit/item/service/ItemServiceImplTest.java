package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.storage.CommentStorage;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemStorage itemStorage;
    @Mock
    private UserStorage userStorage;
    @Mock
    private CommentStorage commentStorage;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private BookingStorage bookingStorage;
    @InjectMocks
    private ItemServiceImpl itemService;
    private ItemDto itemDto;
    private User user;
    private Item item;

    @BeforeEach
    public void beforeEach() {
        itemDto = ItemDto.builder()
                .id(0)
                .name("сварной аппарат")
                .description("самый классный сварной аппарат, сам электроды подаёт")
                .available(Boolean.TRUE)
                .requestId(1L).build();
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
    }

    //должен бросить исключение при создании вещи когда поле name = ""
    @Test
    public void shouldThrowValidExceptionWhenCreateWithEmptyName() {
        //заменили поле на пустое значение
        itemDto.setName("");
        ValidException exception = assertThrows(
                ValidException.class,
                () -> itemService.create(itemDto, 1L));
        assertThat(exception.getMessage()).isEqualTo("Поля не должны быть пустыми или равняться null!");
    }

    //должен бросить исключение если пользователь создающий вещь не существует
    @Test
    public void shouldThrowObjectNotFoundExceptionIfUserNotExistWhenCreateRequest() {
        Mockito.when(userStorage.findById(anyLong())).thenReturn(Optional.empty());
        ObjectNotFoundException exception = assertThrows(
                ObjectNotFoundException.class,
                () -> itemService.create(itemDto, 1L));
        assertThat(exception.getNameObject()).isEqualTo("пользователь");
        assertThat(exception.getIdObject()).isEqualTo(1L);
    }

    //должен бросить исключение при обновлении если id владельца вещи и id пользователя
    //который запрашивает обновление не равны
    @Test
    public void shouldThrowObjectNotFoundExceptionIfOwnerNotEqualsUser() {
        Mockito.when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        ObjectNotFoundException exception = assertThrows(
                ObjectNotFoundException.class,
                () -> itemService.update(itemDto, 1L, 100L));
        assertThat(exception.getNameObject()).isEqualTo("вещь с  id - (1) и владельцем id - (100)");
    }

    @Test
    public void shouldReturnItemDtoFromRequesterWithOutLastAndNextBooking() throws ObjectNotFoundException {
        //нам не важно что возвращают методы, важно знать что у нас не было ни одного обращения к BookingStorage
        //если обращений не было, значит собираем версию не для владельца
        //when
        Mockito.when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        Mockito.when(commentStorage.findAllByItemIdOrderByCreatedDesc(anyLong())).thenReturn(null);
        Mockito
                .when(itemMapper.toResponseItemDto(item, null, null, null))
                .thenReturn(null);
        itemService.findById(1L, 3L); //делаем обращение к методу, что вернёт не важно
        //должен начать формировать версию DTO не для владельца и не ходить в bookingStorage,
        // и один раз сходить в itemMapper
        Mockito.verify(bookingStorage, Mockito.never()).findLastBooking(anyLong(), any(LocalDateTime.class));
        Mockito.verify(bookingStorage, Mockito.never()).findNextBooking(anyLong(), any(LocalDateTime.class));
        Mockito
                .verify(itemMapper, Mockito.atMost(1))
                .toResponseItemDto(any(), any(), any(), anySet());
    }

}