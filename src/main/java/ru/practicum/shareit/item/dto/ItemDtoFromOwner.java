package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
public class ItemDtoFromOwner {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private LastBooking lastBooking;
    private NextBooking nextBooking;

    @Builder @Getter @Setter
    public static class LastBooking {
        private Long id;
        private Long bookerId;

        public LastBooking(Long id, Long bookerId) {
            this.id = id;
            this.bookerId = bookerId;
        }
    }

    @Builder @Getter @Setter
    public static class NextBooking {
        private Long id;
        private Long bookerId;

        public NextBooking(Long id, Long bookerId) {
            this.id = id;
            this.bookerId = bookerId;
        }
    }
}
