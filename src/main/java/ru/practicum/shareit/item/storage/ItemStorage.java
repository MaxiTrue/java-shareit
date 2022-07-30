package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long userId);

    @Query(
            "SELECT i FROM Item i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', ?1, '%'))" +
                    "OR LOWER(i.description) LIKE LOWER(CONCAT('%', ?1, '%')) AND i.available = true"
    )
    List<Item> searchByNameAndDescription(String text);

}
