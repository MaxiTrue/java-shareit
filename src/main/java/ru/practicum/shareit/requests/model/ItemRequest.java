package ru.practicum.shareit.requests.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequest {
    private long id;
    private String description;
    private User requester;
    private LocalDateTime created;
}
