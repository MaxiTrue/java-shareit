package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;

@RestController
@Validated
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") @NotNull long userId)
            throws ObjectNotFoundException, ValidException {
        log.debug("Получен запрос POST на создание вещи от пользователя id - {}", userId);
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto partialUpdate(
            @RequestBody ItemDto itemDto,
            @PathVariable("itemId") long itemId,
            @RequestHeader("X-Sharer-User-Id") @NotNull long userId) throws ObjectNotFoundException, ValidException {
        log.debug("Получен запрос PATCH на обновление данных вещи от пользователя id - {}", userId);
        return itemService.update(itemDto, itemId, userId);
    }

    @GetMapping
    public Collection<ResponseItemDto> findAllByUserId(
            @RequestHeader("X-Sharer-User-Id") @NotNull long userId,
            @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
            @RequestParam(name = "size", defaultValue = "5") @Min(1) int size) throws ObjectNotFoundException {
        log.debug("Получен запрос GET на получение всех вещей пользователя id - {}", userId);
        Pageable pageable = PageRequest.of(from, size);
        return itemService.findAllByUserId(userId, pageable);
    }

    @GetMapping("/{itemId}")
    public ResponseItemDto findById(
            @PathVariable("itemId") long id,
            @RequestHeader("X-Sharer-User-Id") @NotNull long userId) throws ObjectNotFoundException {
        log.debug("Получен запрос GET на получение вещи id - {}", id);
        return itemService.findById(id, userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> findBySearch(
            @RequestParam("text") String text,
            @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
            @RequestParam(name = "size", defaultValue = "5") @Min(1) int size) {
        log.debug("Получен запрос GET на поиск вещи по тексту - {}", text);
        String textResult = text.toLowerCase().replaceAll("\\s", "");
        if (textResult.isEmpty()) return new ArrayList<>();
        Pageable pageable = PageRequest.of(from, size);
        return itemService.findBySearch(textResult, pageable);
    }

}
