package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

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
    private final ItemMapper itemMapper;

    @PostMapping
    public ItemDto create(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") @NotNull long userId)
            throws Throwable {
        log.debug("Получен запрос POST на создание вещи от пользователя id - {}", userId);
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto partialUpdate(@RequestBody ItemDto itemDto,
                                 @PathVariable("itemId") long itemId,
                                 @RequestHeader("X-Sharer-User-Id") @NotNull long userId) throws Throwable {
        log.debug("Получен запрос PATCH на обновление данных вещи от пользователя id - {}", userId);
        ItemDto fullItemDto = itemMapper.toItemDtoFromPartialUpdate(itemDto, itemId, userId);
        return itemService.update(fullItemDto, userId);
    }

    @GetMapping
    public Collection<ItemDto> getAllByUserId(@RequestHeader("X-Sharer-User-Id") @NotNull long userId) {
        log.debug("Получен запрос GET на получение всех вещей пользователя id - {}", userId);
        return itemService.getAllByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable("itemId") long id) throws Throwable {
        log.debug("Получен запрос GET на получение вещи id - {}", id);
        return itemService.getById(id);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getBySearch(@RequestParam("text") String text) {
        log.debug("Получен запрос GET на поиск вещи по тексту - {}", text);
        String textResult = text.toLowerCase().replaceAll("\\s", "");
        if (textResult.isEmpty()) return new ArrayList<>();
        return itemService.getBySearch(textResult);
    }

}
