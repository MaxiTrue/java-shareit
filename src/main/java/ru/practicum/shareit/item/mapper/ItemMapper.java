package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemMapper {

    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    public Item toEntityItem(ItemDto itemDto, long userId) throws Throwable {
        User user = userStorage.getById(userId)
                .orElseThrow((Supplier<Throwable>) () -> new ObjectNotFoundException("пользователь", userId));

        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(user).build();

    }

    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable()).build();
    }

    public Collection<ItemDto> toListItemDto(Collection<Item> items) {
        return items.stream().map(this::toItemDto).collect(Collectors.toList());
    }

    public ItemDto toItemDtoFromPartialUpdate(ItemDto itemDto, long itemId, long userId) throws Throwable {
        Item itemFromStorage = itemStorage
                .getById(itemId).orElseThrow((Supplier<Throwable>) () -> new ObjectNotFoundException("вещь", itemId));

        if (itemFromStorage.getOwner().getId() != userId) {
            throw new ObjectNotFoundException(
                    String.format("вещь с  id - (%d) и владельцем id - (%d)",
                            itemFromStorage.getId(), userId));
        }

        ItemDto copyItemDto = itemDto;

        copyItemDto.setId(itemFromStorage.getId());

        if (copyItemDto.getName() == null) {
            copyItemDto.setName(itemFromStorage.getName());
        }

        if (copyItemDto.getDescription() == null) {
            copyItemDto.setDescription(itemFromStorage.getDescription());
        }

        if (copyItemDto.getAvailable() == null) {
            copyItemDto.setAvailable(itemFromStorage.getAvailable());
        }

        return copyItemDto;
    }

}
