package ru.practicum.shareit.booking.json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.StateBooking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testUserDto() throws Exception {
        LocalDateTime start = LocalDateTime.of(
                LocalDate.of(2022, Month.AUGUST, 1),
                LocalTime.of(10, 10, 10));
        LocalDateTime end = start.plusDays(1);
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .status(StateBooking.WAITING)
                .bookerId(2L)
                .itemId(3L)
                .start(start)
                .end(end).build();
        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2022-08-01T10:10:10");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2022-08-02T10:10:10");
    }
}
