package ru.practicum.shareit.requests.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestWithItemDto {
    private long id;
    private String description;
    private LocalDateTime created = LocalDateTime.now();
    private Set<ItemFromItemRequest> items;

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    public static class ItemFromItemRequest {
        private Long id;
        private String name;
        private String description;
        private Boolean available;
        private Long requestId;
        private Long ownerId;
    }
}
