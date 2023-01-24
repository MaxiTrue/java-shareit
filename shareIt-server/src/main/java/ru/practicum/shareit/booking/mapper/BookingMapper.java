package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto.Booker;
import ru.practicum.shareit.booking.dto.ResponseBookingDto.ItemB;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingMapper {

    public Booking toEntityBooking(BookingDto bookingDto, User booker, Item item) {
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(bookingDto.getStatus());
        return booking;
    }

    public ResponseBookingDto toResponseBookingDto(Booking booking) {
        Booker booker = Booker.builder()
                .id(booking.getBooker().getId())
                .name(booking.getBooker().getName()).build();
        ItemB itemB = ItemB.builder()
                .id(booking.getItem().getId())
                .name(booking.getItem().getName()).build();

        return ResponseBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(itemB)
                .booker(booker)
                .status(booking.getStatus()).build();
    }

    public List<ResponseBookingDto> toResponseBookingDto(Collection<Booking> bookings) {
        return bookings.stream().map(this::toResponseBookingDto).collect(Collectors.toList());
    }
}
