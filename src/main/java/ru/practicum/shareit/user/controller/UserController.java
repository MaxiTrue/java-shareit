package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) throws ValidException {
        log.debug("Получен запрос POST на создание пользователя с email - {}", userDto.getEmail());
        return userService.create(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable("userId") long id)
            throws ObjectNotFoundException, ValidException {
        log.debug("Получен запрос PATCH на частичное обновление данны пользователя id - {}", id);
        return userService.update(userDto, id);
    }

    @DeleteMapping("/{userId}")
    public Long delete(@PathVariable("userId") long id) throws ObjectNotFoundException {
        log.debug("Получен запрос DELETE на удаление пользователя id - {}", id);
        return userService.delete(id);
    }

    @GetMapping
    public Collection<UserDto> findAll() {
        log.debug("Получен запрос GET на получение всех пользователей");
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable("userId") long id) throws ObjectNotFoundException {
        log.debug("Получен запрос GET на получение пользователя по id - {}", id);
        return userService.findById(id);
    }

}
