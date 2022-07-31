package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto itemDto, long userId) throws ObjectNotFoundException, ValidException;

    ItemDto update(ItemDto itemDto, long itemId, long userId) throws ObjectNotFoundException, ValidException;

    List<ResponseItemDto> getAllByUserId(long userId);

    ResponseItemDto getById(long id, long userId) throws ObjectNotFoundException;

    List<ItemDto> getBySearch(String text);
}
