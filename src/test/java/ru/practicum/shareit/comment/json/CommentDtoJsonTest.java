package ru.practicum.shareit.comment.json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CommentDtoJsonTest {
    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testUserDto() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .authorName("Max")
                .text("добро")
                .itemId(1L)
                .created(LocalDateTime.of(
                        LocalDate.of(2022, Month.AUGUST, 1),
                        LocalTime.of(10, 10, 10))).build();

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Max");
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("добро");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2022-08-01T10:10:10");
    }
}
