package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Long, Item> items = new ConcurrentHashMap<>();
    private static long globalId = 0;

    private long generateNewId() {
        return ++globalId;
    }

    @Override
    public Item create(Item item) {
        item.setId(generateNewId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Collection<Item> getAllByUserId(long userId) {
        return items.values().stream().filter(item -> item.getOwner().getId() == userId).collect(Collectors.toList());
    }

    @Override
    public Optional<Item> getById(long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Collection<Item> getBySearch(String text) {
        return items.values().stream()
                .filter(item -> {
                    String nameResult = item.getName().toLowerCase().replaceAll("\\s", "");
                    String descriptionResult = item.getDescription().toLowerCase().replaceAll("\\s", "");
                    return nameResult.contains(text) || descriptionResult.contains(text);
                })
                .filter(Item::getAvailable).collect(Collectors.toList());
    }
}
