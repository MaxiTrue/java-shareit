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
        User user = userMapper.toUserEntity(userDto);
        return userMapper.toUserDto(userStorage.save(user));
    }

    @Override
    public UserDto update(UserDto userDto, long id) throws ObjectNotFoundException, ValidException {
        User updateUser = userStorage.findById(id).orElseThrow(() -> new ObjectNotFoundException("пользователь", id));
        if (userDto.getName() != null) updateUser.setName(userDto.getName());
        if (userDto.getEmail() != null) updateUser.setEmail(userDto.getEmail());
        return userMapper.toUserDto(userStorage.save(updateUser));
    }

    @Override
    public Long delete(long id) throws ObjectNotFoundException {
        User user = userStorage.findById(id).orElseThrow(() -> new ObjectNotFoundException("пользователь", id));
        userStorage.delete(user);
        return id;
    }

    @Override
    public List<UserDto> findAll() {
        return userMapper.toListResponseUserDto(userStorage.findAll());
    }

    @Override
    public UserDto findById(long id) throws ObjectNotFoundException {
        User user = userStorage.findById(id).orElseThrow(() -> new ObjectNotFoundException("пользователь", id));
        return userMapper.toUserDto(user);
    }

}


