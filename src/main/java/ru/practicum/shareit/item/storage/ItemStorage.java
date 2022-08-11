package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Long> {

    Page<Item> findAllByOwnerIdOrderByIdAsc(Long userId, Pageable pageable);

    @Query("SELECT i FROM Item i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%'))" +
                    "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%')) AND i.available = true")
    Page<Item> searchByNameAndDescription(String text, Pageable pageable);

    List<Item> findAllByRequestId(long requestId);

}
