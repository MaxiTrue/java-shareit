package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.Collection;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemStorage itemStorage;
    private final ItemMapper itemMapper;

    public ItemDto create(ItemDto itemDto, long userId) throws Throwable {
        validItemDto("POST", itemDto);
        Item item = itemMapper.toEntityItem(itemDto, userId);
        return itemMapper.toItemDto(itemStorage.create(item));
    }

    public ItemDto update(ItemDto itemDto, long userId) throws Throwable {
        validItemDto("PATH", itemDto);
        Item item = itemMapper.toEntityItem(itemDto, userId);
        return itemMapper.toItemDto(itemStorage.update(item));
    }

    public Collection<ItemDto> getAllByUserId(long userId) {
        return itemMapper.toListItemDto(itemStorage.getAllByUserId(userId));
    }

    public ItemDto getById(long id) throws Throwable {
        Item item = itemStorage.getById(id)
                .orElseThrow((Supplier<Throwable>) () -> new ObjectNotFoundException("вещь", id));
        return itemMapper.toItemDto(item);
    }

    public Collection<ItemDto> getBySearch(String text) {
        return itemMapper.toListItemDto(itemStorage.getBySearch(text));
    }

    private void validItemDto(String method, ItemDto itemDto) throws ObjectNotFoundException, ValidException {

        if (!method.equals("POST")) {
            if (itemDto.getId() <= 0) {
                throw new ObjectNotFoundException("вещь", itemDto.getId());
            }
        }

        if (itemDto.getName() == null || itemDto.getDescription() == null || itemDto.getAvailable() == null) {
            throw new ValidException("Поля не должны равняться null!");
        }

        if (itemDto.getName().isBlank() || itemDto.getDescription().isBlank()) {
            throw new ValidException("Поля не должны быть пустыми или равняться null!");
        }

        if (itemDto.getName().length() < 3 || itemDto.getName().length() > 50) {
            throw new ValidException("Поле name не соответствует допустимому размеру!");
        }

        if (itemDto.getDescription().length() > 300) {
            throw new ValidException("Поле description не соответствует допустимому размеру!");
        }

    }

}
