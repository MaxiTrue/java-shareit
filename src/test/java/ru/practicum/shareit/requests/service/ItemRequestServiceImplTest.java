package ru.practicum.shareit.requests.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private UserStorage userStorage;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    private static ItemRequestDto itemRequestDto;

    @BeforeAll
    public static void beforeAll(){
        itemRequestDto = ItemRequestDto.builder()
                .id(0)
                .description("Нужен перфоратор")
                .created(LocalDateTime.now()).build();
    }

    //должен бросить исключение когда пользователь не существует при создании запроса
    @Test
    public void shouldThrowObjectNotFoundExceptionIfUserNotExistWhenCreateRequest() {
        Mockito.when(userStorage.findById(anyLong())).thenReturn(Optional.empty());
        ObjectNotFoundException exception = assertThrows(
                ObjectNotFoundException.class,
                () -> itemRequestService.create(itemRequestDto, 1L));
        assertThat(exception.getNameObject()).isEqualTo("пользователь");
        assertThat(exception.getIdObject()).isEqualTo(1L);
    }

    //должен бросить исключение когда пользователь осуществивший запрос не существует
    @Test
    public void shouldThrowObjectNotFoundExceptionWhenTryFindAllRequests() {
        Mockito.when(userStorage.existsById(anyLong())).thenReturn(Boolean.FALSE);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "created"));
        ObjectNotFoundException exception = assertThrows(
                ObjectNotFoundException.class,
                () -> itemRequestService.findAll(1L, pageable));
        assertThat(exception.getNameObject()).isEqualTo("пользователь");
        assertThat(exception.getIdObject()).isEqualTo(1L);
    }

    //должен бросить исключение когда пользователь осуществивший запрос не существует
    @Test
    public void shouldThrowObjectNotFoundExceptionWhenTryFindRequestsById() {
        Mockito.when(userStorage.existsById(anyLong())).thenReturn(Boolean.FALSE);
        ObjectNotFoundException exception = assertThrows(
                ObjectNotFoundException.class,
                () -> itemRequestService.findById(1L, 1L));
        assertThat(exception.getNameObject()).isEqualTo("пользователь");
        assertThat(exception.getIdObject()).isEqualTo(1L);
    }

}