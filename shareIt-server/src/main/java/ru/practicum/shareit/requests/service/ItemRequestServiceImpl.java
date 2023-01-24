package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestWithItemDto;
import ru.practicum.shareit.requests.mapper.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestStorage itemRequestStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, long userId) throws ObjectNotFoundException {
        User requester = userStorage.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("пользователь", userId));
        ItemRequest itemRequest = itemRequestStorage.save(itemRequestMapper.toItemRequest(itemRequestDto, requester));

        return itemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public Set<ItemRequestWithItemDto> findAllByRequesterId(long userId) throws ObjectNotFoundException {
        User requester = userStorage.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("пользователь", userId));
        Set<ItemRequest> itemRequests = itemRequestStorage.findAllByRequesterIdOrderByCreatedDesc(requester.getId());

        return itemRequests.stream().map(itemRequest -> {
            List<Item> items = itemStorage.findAllByRequestId(itemRequest.getId());
            return itemRequestMapper.toItemRequestWithItemDto(itemRequest, items);
        }).collect(Collectors.toSet());
    }

    @Override
    public Set<ItemRequestWithItemDto> findAll(long userId, Pageable pageable) throws ObjectNotFoundException {
        if (!userStorage.existsById(userId)) throw new ObjectNotFoundException("пользователь", userId);
        Page<ItemRequest> page = itemRequestStorage.findAllByRequesterIdNot(userId, pageable);

        return page.get().map(itemRequest -> {
            List<Item> items = itemStorage.findAllByRequestId(itemRequest.getId());
            return itemRequestMapper.toItemRequestWithItemDto(itemRequest, items);
        }).collect(Collectors.toSet());
    }

    @Override
    public ItemRequestWithItemDto findById(long requestId, long userId) throws ObjectNotFoundException {
        if (!userStorage.existsById(userId)) throw new ObjectNotFoundException("пользователь", userId);
        ItemRequest itemRequest = itemRequestStorage
                .findById(requestId).orElseThrow(() -> new ObjectNotFoundException("запрос", requestId));
        List<Item> items = itemStorage.findAllByRequestId(itemRequest.getId());
        return itemRequestMapper.toItemRequestWithItemDto(itemRequest, items);
    }

}
