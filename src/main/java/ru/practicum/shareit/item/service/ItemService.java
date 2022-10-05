package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto itemDto, long userId) throws ObjectNotFoundException, ValidException;

    ItemDto update(ItemDto itemDto, long itemId, long userId) throws ObjectNotFoundException, ValidException;

    List<ResponseItemDto> findAllByUserId(long userId, Pageable pageable) throws ObjectNotFoundException;

    ResponseItemDto findById(long itemId, long userId) throws ObjectNotFoundException;

    List<ItemDto> findBySearch(String text, Pageable pageable);
}
