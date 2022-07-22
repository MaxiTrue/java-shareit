package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    User create(User user);

    User update(User user);

    Long delete(long id);

    Collection<User> getAll();

    Optional<User> getById(long id);

    Optional<User> getByEmail(String email);

}
