package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage extends JpaRepository<Item, Long> {

    Collection<Item> findAllByOwnerId(Long userId);

    @Query(
            "SELECT i FROM Item i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', ?1, '%'))" +
                    "OR LOWER(i.description) LIKE LOWER(CONCAT('%', ?1, '%')) AND i.available = true"
    )
    Collection<Item> searchByNameAndDescription(String text);

}
