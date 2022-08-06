package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingFromItem lastBooking;
    private BookingFromItem nextBooking;
    private Set<CommentFromItem> comments;

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    public static class BookingFromItem {
        private Long id;
        private Long bookerId;
    }

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    public static class CommentFromItem {
        private Long id;
        private String text;
        private String authorName;
        private LocalDateTime create;
    }

}
