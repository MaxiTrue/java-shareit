package ru.practicum.shareit.requests.json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testUserDto() throws Exception {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("test")
                .created(LocalDateTime.of(
                        LocalDate.of(2022, Month.AUGUST, 1),
                        LocalTime.of(10, 10, 10))).build();

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2022-08-01T10:10:10");
    }
}
