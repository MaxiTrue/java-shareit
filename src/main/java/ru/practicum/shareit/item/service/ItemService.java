package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemMapper itemMapper;

    public ItemDto create(ItemDto itemDto, long userId) throws Throwable {
        validItemDto(itemDto);
        User user = userStorage.findById(userId)
                .orElseThrow((Supplier<Throwable>) () -> new ObjectNotFoundException("пользователь", userId));
        Item item = itemMapper.toEntityItem(itemDto, user);
        return itemMapper.toItemDto(itemStorage.save(item));
    }

    public ItemDto update(ItemDto itemDto, long itemId, long userId) throws Throwable {
        //проверяем наличие обновляемого объекта, если существует то получаем для мапинга в единый DTO объект
        Item itemFromStorage = itemStorage
                .findById(itemId).orElseThrow((Supplier<Throwable>) () -> new ObjectNotFoundException("вещь", itemId));

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
                .orElseThrow((Supplier<Throwable>) () -> new ObjectNotFoundException("пользователь", userId));
        Item item = itemMapper.toEntityItem(itemDto, user);
        return itemMapper.toItemDto(itemStorage.save(item));
    }

    public Collection<ItemDto> getAllByUserId(long userId) {
        return itemMapper.toListItemDto(itemStorage.findAllByOwnerId(userId));
    }

    public ItemDto getById(long id) throws Throwable {
        Item item = itemStorage.findById(id)
                .orElseThrow((Supplier<Throwable>) () -> new ObjectNotFoundException("вещь", id));
        return itemMapper.toItemDto(item);
    }

    public Collection<ItemDto> getBySearch(String text) {
        return itemMapper.toListItemDto(itemStorage.searchByNameAndDescription(text));
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
