package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Validated({Create.class}) @RequestBody UserDto userDto) {
        log.debug("Получен запрос POST на создание пользователя с email - {}", userDto.getEmail());
        return userClient.create(userDto);
    }

    @PatchMapping("{userId}")
    public ResponseEntity<Object> update(@Validated({Update.class}) @RequestBody UserDto userDto,
                                         @PathVariable("userId") long userId) {
        log.debug("Получен запрос PATCH на частичное обновление данны пользователя id - {}", userId);
        return userClient.update(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable("userId") long userId) {
        log.debug("Получен запрос DELETE на удаление пользователя id - {}", userId);
        return userClient.delete(userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.debug("Получен запрос GET на получение всех пользователей");
        return userClient.findAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable("userId") long userId) {
        log.debug("Получен запрос GET на получение пользователя по id - {}", userId);
        return userClient.findById(userId);
    }

}
