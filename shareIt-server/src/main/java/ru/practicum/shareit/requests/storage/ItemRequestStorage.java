package ru.practicum.shareit.requests.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.Set;

public interface ItemRequestStorage extends JpaRepository<ItemRequest, Long> {

    Set<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(long requesterId);

    Page<ItemRequest> findAllByRequesterIdNot(long requesterId, Pageable pageable);

}
