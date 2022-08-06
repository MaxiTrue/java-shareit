package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserDto userDto) throws ValidException {
        validUserDto(userDto);
        User user = userMapper.toUserEntity(userDto);
        return userMapper.toUserDto(userStorage.save(user));
    }

    @Override
    public UserDto update(UserDto userDto, long id)
            throws ObjectNotFoundException, ValidException {
        //проверяем наличие обновляемого объекта, если существует то получаем для мапинга в единый DTO объект
        User user = userStorage.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("пользователь", id));
        UserDto fullUserDto = userMapper.toUserDtoFromPartialUpdate(userDto, user);

        //валидация собранного объекта
        validUserDto(fullUserDto);

        //собираем объект для хранения в БД
        User userFromStorage = userMapper.toUserEntity(userDto);
        return userMapper.toUserDto(userStorage.save(userFromStorage));
    }

    @Override
    public Long delete(long id) throws ObjectNotFoundException {
        User user = userStorage.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("пользователь", id));
        userStorage.delete(user);
        return id;
    }

    @Override
    public List<UserDto> findAll() {
        return userMapper.toListResponseUserDto(userStorage.findAll());
    }

    @Override
    public UserDto findById(long id) throws ObjectNotFoundException {
        User user = userStorage.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("пользователь", id));
        return userMapper.toUserDto(user);
    }

    private void validUserDto(UserDto userDto) throws ValidException {

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

    }

}


