package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.storage.CommentStorage;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final CommentStorage commentStorage;
    private final ItemRequestStorage itemRequestStorage;

    private final ItemMapper itemMapper;

    @Override
    public ItemDto create(ItemDto itemDto, long userId) throws ObjectNotFoundException, ValidException {
        User user = userStorage.findById(userId).orElseThrow(() -> new ObjectNotFoundException("пользователь", userId));

        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = itemRequestStorage.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new ObjectNotFoundException("запрос", itemDto.getRequestId()));
        }

        Item item = itemMapper.toEntityItem(itemDto, user, itemRequest);
        return itemMapper.toItemDto(itemStorage.save(item));
    }

    @Override
    public ItemDto update(ItemDto itemDto, long itemId, long userId) throws ObjectNotFoundException, ValidException {
        //проверяем наличие обновляемого объекта, если существует то получаем для мапинга в единый DTO объект
        Item itemUpdate = itemStorage
                .findById(itemId).orElseThrow(() -> new ObjectNotFoundException("вещь", itemId));

        //проверяем является ли пользователь с пришедшим id автором поста
        if (itemUpdate.getOwner().getId() != userId) {
            throw new ObjectNotFoundException(
                    String.format("вещь с  id - (%d) и владельцем id - (%d)", itemUpdate.getId(), userId));
        }

        if (itemDto.getName() != null) itemUpdate.setName(itemDto.getName());
        if (itemDto.getDescription() != null) itemUpdate.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) itemUpdate.setAvailable(itemDto.getAvailable());
        return itemMapper.toItemDto(itemStorage.save(itemUpdate));
    }

    @Override
    public List<ResponseItemDto> findAllByUserId(long userId, Pageable pageable) throws ObjectNotFoundException {
        if (!userStorage.existsById(userId)) throw new ObjectNotFoundException("пользователь", userId);

        return itemStorage.findAllByOwnerIdOrderByIdAsc(userId, pageable).get().map(item -> {
            LocalDateTime now = LocalDateTime.now();
            Optional<Booking> lastBooking = bookingStorage.findLastBooking(item.getId(), now);
            Optional<Booking> nextBooking = bookingStorage.findNextBooking(item.getId(), now);
            Set<Comment> comments = commentStorage.findAllByItemIdOrderByCreatedDesc(item.getId());
            return itemMapper.toResponseItemDto(
                    item,
                    lastBooking.orElse(null),
                    nextBooking.orElse(null),
                    comments);
        }).collect(Collectors.toList());
    }

    @Override
    public ResponseItemDto findById(long itemId, long userId) throws ObjectNotFoundException {
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("вещь", itemId));

        //получаем комментарии
        Set<Comment> comments = commentStorage.findAllByItemIdOrderByCreatedDesc(item.getId());

        //формируем ответ для владельца вещи
        if (item.getOwner().getId() == userId) {
            LocalDateTime now = LocalDateTime.now();
            Optional<Booking> lastBooking = bookingStorage.findLastBooking(item.getId(), now);
            Optional<Booking> nextBooking = bookingStorage.findNextBooking(item.getId(), now);

            return itemMapper.toResponseItemDto(
                    item,
                    lastBooking.orElse(null),
                    nextBooking.orElse(null),
                    comments);

        }
        return itemMapper.toResponseItemDto(item, null, null, comments);
    }

    @Override
    public List<ItemDto> findBySearch(String text, Pageable pageable) {
        return itemMapper.toListItemDto(itemStorage.searchByNameAndDescription(text, pageable).getContent());
    }

}
