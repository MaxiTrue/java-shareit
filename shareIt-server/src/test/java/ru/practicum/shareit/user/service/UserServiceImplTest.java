package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserStorage userStorage;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;

    //должен бросить исключение когда не нашёл объект в БД при обновлении
    @Test
    public void shouldThrowObjectNotFoundExceptionWhenNotFindUser() {
        Mockito.when(userStorage.findById(anyLong())).thenReturn(Optional.empty());
        ObjectNotFoundException exception = assertThrows(
                ObjectNotFoundException.class,
                () -> userService.update(makeUserDto(0, "MaxUpdate", "maxiTrue@gmail.ru"), 1L));
        assertThat(exception.getNameObject()).isEqualTo("пользователь");
        assertThat(exception.getIdObject()).isEqualTo(1L);
    }

    //должен вернуть id удалённого объекта, проверяем был ли вызов метода удаления
    @Test
    public void shouldReturnIdRemoteObject() throws ObjectNotFoundException {
        User user = makeUser(1, "Max", "maxiTrue@gmail.ru");
        Mockito.when(userStorage.findById(1L)).thenReturn(Optional.of(user));
        long idRemoteUser = userService.delete(1L);
        assertThat(idRemoteUser).isEqualTo(1L);
        Mockito.verify(userStorage, Mockito.times(1)).delete(user);
    }


    private User makeUser(long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private UserDto makeUserDto(long id, String name, String email) {
        return UserDto.builder().id(id).name(name).email(email).build();
    }

}