package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.StateBooking;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseBookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemB item;
    private Booker booker;
    private StateBooking status;

    @Builder
    @Getter
    @Setter
    public static class Booker {
        private Long id;
        private String name;

        public Booker(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Builder
    @Getter
    @Setter
    public static class ItemB {
        private Long id;
        private String name;

        public ItemB(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
