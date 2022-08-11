package ru.practicum.shareit.requests.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestWithItemDto;

import java.util.Set;

public interface ItemRequestService {

    ItemRequestDto create(ItemRequestDto itemRequestDto, long userId) throws ObjectNotFoundException;

    Set<ItemRequestWithItemDto> findAllByRequesterId(long userId) throws ObjectNotFoundException;

    Set<ItemRequestWithItemDto> findAll(long userId, Pageable pageable) throws ObjectNotFoundException;

    ItemRequestWithItemDto findById(long requestId, long userId) throws ObjectNotFoundException;
}
