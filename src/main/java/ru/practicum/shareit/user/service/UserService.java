package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotUniqueEmailException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final UserMapper userMapper;

    public UserDto create(UserDto userDto) throws Throwable {
        validUserDto(userDto);
        User user = userMapper.toUserEntity(userDto);
        return userMapper.toUserDto(userStorage.create(user));
    }

    public UserDto update(UserDto userDto, long id) throws Throwable {
        //проверяем наличие обновляемого объекта, если существует то получаем для мапинга в единый DTO объект
        User user = userStorage.getById(id)
                .orElseThrow((Supplier<Throwable>) () -> new ObjectNotFoundException("пользователь", id));
        UserDto fullUserDto = userMapper.toUserDtoFromPartialUpdate(userDto, user);

        //валидация собранного объекта
        validUserDto(fullUserDto);

        //собираем объект для хранения в БД
        User userFromStorage = userMapper.toUserEntity(userDto);
        return userMapper.toUserDto(userStorage.update(userFromStorage));
    }

    public Long delete(long id) throws Throwable {
        userStorage.getById(id)
                .orElseThrow((Supplier<Throwable>) () -> new ObjectNotFoundException("пользователь", id));
        return userStorage.delete(id);
    }

    public Collection<UserDto> getAllByUserId() {
        return userMapper.toListResponseUserDto(userStorage.getAll());
    }

    public UserDto getById(long id) throws Throwable {
        User user = userStorage.getById(id)
                .orElseThrow((Supplier<Throwable>) () -> new ObjectNotFoundException("пользователь", id));
        return userMapper.toUserDto(user);
    }

    private void validUserDto(UserDto userDto) throws ValidException, NotUniqueEmailException {

        if (userDto.getName() == null || userDto.getEmail() == null) {
            throw new ValidException("Поля не должны равняться null!");
        }

        if (userDto.getName().isBlank() || userDto.getEmail().isBlank()) {
            throw new ValidException("Поля не должны быть пустыми или равняться null!");
        }

        if (userDto.getName().length() < 3 || userDto.getName().length() > 30) {
            throw new ValidException("Поле name не соответствует допустимому размеру!");
        }

        if (!userDto.getEmail().contains("@")) {
            throw new ValidException("Поле email не соответствует нужному формату!");
        }

        Optional<User> user = userStorage.getByEmail(userDto.getEmail());

        if (user.isPresent()) {
            if (user.get().getId() != userDto.getId()) {
                throw new NotUniqueEmailException("Этот email - " + userDto.getEmail() + " уже используется!");
            }
        }

    }

}


