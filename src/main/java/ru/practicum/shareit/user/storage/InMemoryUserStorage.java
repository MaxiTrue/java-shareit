package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new ConcurrentHashMap<>();

    private static long globalId = 0;

    private long generateNewId() {
        return ++globalId;
    }

    @Override
    public User create(User user) {
        user.setId(generateNewId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Long delete(long id) {
        users.remove(id);
        return id;
    }

    @Override
    public Collection<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return users.values().stream().filter(user1 -> user1.getEmail().equals(email)).findFirst();
    }

}
