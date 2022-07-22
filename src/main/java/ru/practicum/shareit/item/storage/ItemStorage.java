package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {

    Item create(Item item);

    Item update(Item item);

    Collection<Item> getAllByUserId(long userId);

    Optional<Item> getById(long id);

    Collection<Item> getBySearch(String text);

}
