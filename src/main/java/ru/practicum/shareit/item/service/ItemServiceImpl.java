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
        validItemDto(itemDto);
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("пользователь", userId));
                Item item = itemMapper.toEntityItem(itemDto, user, findItemRequestFromItemDto(itemDto));
        return itemMapper.toItemDto(itemStorage.save(item));
    }

    @Override
    public ItemDto update(ItemDto itemDto, long itemId, long userId) throws ObjectNotFoundException, ValidException {
        //проверяем наличие обновляемого объекта, если существует то получаем для мапинга в единый DTO объект
        Item itemFromStorage = itemStorage
                .findById(itemId).orElseThrow(() -> new ObjectNotFoundException("вещь", itemId));

        //проверяем является ли пользователь с пришедшим id автором поста
        if (itemFromStorage.getOwner().getId() != userId) {
            throw new ObjectNotFoundException(
                    String.format("вещь с  id - (%d) и владельцем id - (%d)",
                            itemFromStorage.getId(), userId));
        }
        ItemDto fullItemDto = itemMapper.toItemDtoFromPartialUpdate(itemDto, itemFromStorage);

        //валидация собранного объекта
        validItemDto(fullItemDto);

        //собираем объект для хранения в БД
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("пользователь", userId));
        Item item = itemMapper.toEntityItem(itemDto, user, findItemRequestFromItemDto(itemDto));
        return itemMapper.toItemDto(itemStorage.save(item));
    }

    @Override
    public List<ResponseItemDto> findAllByUserId(long userId, Pageable pageable) throws ObjectNotFoundException {
        if(!userStorage.existsById(userId)) throw new ObjectNotFoundException("пользователь", userId);

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

    //WARNING метод может вернуть NULL
    private ItemRequest findItemRequestFromItemDto(ItemDto itemDto) throws ObjectNotFoundException {
        if (itemDto.getRequestId() == null) return null;
        return itemRequestStorage.findById(itemDto.getRequestId())
                .orElseThrow(() -> new ObjectNotFoundException("запрос", itemDto.getRequestId()));

    }

    private void validItemDto(ItemDto itemDto) throws ValidException {

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
