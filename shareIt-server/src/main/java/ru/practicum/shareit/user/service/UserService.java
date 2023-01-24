package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto userDto) throws ValidException;

    UserDto update(UserDto userDto, long id) throws ObjectNotFoundException, ValidException;

    Long delete(long id) throws ObjectNotFoundException;

    List<UserDto> findAll();

    UserDto findById(long id) throws ObjectNotFoundException;
}
