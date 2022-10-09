package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;

@RestController
@Validated
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@Validated({Create.class}) @RequestBody ItemDto itemDto,
                                         @RequestHeader("X-Sharer-User-Id") @NotNull long userId) {
        log.debug("Получен запрос POST на создание вещи от пользователя id - {}", userId);
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> partialUpdate(@Validated({Update.class}) @RequestBody ItemDto itemDto,
                                                @PathVariable("itemId") long itemId,
                                                @RequestHeader("X-Sharer-User-Id") @NotNull long userId) {
        log.debug("Получен запрос PATCH на обновление данных вещи от пользователя id - {}", userId);
        return itemClient.partialUpdate(itemId, userId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(@RequestHeader("X-Sharer-User-Id") @NotNull long userId,
                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                  @Positive @RequestParam(name = "size", defaultValue = "5") int size) {
        log.debug("Получен запрос GET на получение всех вещей пользователя id - {}", userId);
        return itemClient.findAllByUserId(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@PathVariable("itemId") long itemId,
                                           @RequestHeader("X-Sharer-User-Id") @NotNull long userId) {
        log.debug("Получен запрос GET на получение вещи id - {}", itemId);
        return itemClient.findById(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findBySearch(@RequestParam("text") String text,
                                               @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                               @Positive @RequestParam(name = "size", defaultValue = "5") int size) {
        log.debug("Получен запрос GET на поиск вещи по тексту - {}", text);
        String textResult = text.toLowerCase().replaceAll("\\s", "");
        if (textResult.isEmpty()) return ResponseEntity.status(200).body(new ArrayList<>());
        return itemClient.findBySearch("/search", textResult, from, size);
    }

}
