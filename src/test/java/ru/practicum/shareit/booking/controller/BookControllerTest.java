package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.StateBooking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidException;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookingService bookingService;
    @InjectMocks
    private BookController controller;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private ResponseBookingDto responseBookingDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(ErrorHandler.class).build();
        responseBookingDto = ResponseBookingDto.builder()
                .id(1L)
                .start(null)
                .end(null)
                .item(null)
                .booker(null)
                .status(StateBooking.WAITING).build();
    }

    @Test
    public void createBookingCodeIs_200() throws Exception {
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .itemId(1L)
                .bookerId(1L)
                .start(null)
                .end(null)
                .status(StateBooking.WAITING).build();
        Mockito.when(bookingService.create(any(BookingDto.class), anyLong())).thenReturn(responseBookingDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseBookingDto.getId()), Long.class));
    }

    @Test
    public void createBookingCodeIs_400_WhenNotValidValue() throws Exception {
        Mockito.when(bookingService.create(any(BookingDto.class), anyLong())).thenThrow(ValidException.class);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(new BookingDto()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateStatusBookingCodeIs_200() throws Exception {
        Mockito.when(bookingService.updateStatus(anyLong(), anyBoolean(), anyLong())).thenReturn(responseBookingDto);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseBookingDto.getId()), Long.class));
    }

    @Test
    public void updateStatusBookingCodeIs_404_WhenSomethingNotFound() throws Exception {
        Mockito.when(bookingService.updateStatus(anyLong(), anyBoolean(), anyLong()))
                .thenThrow(ObjectNotFoundException.class);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    public void updateStatusBookingCodeIs_400_WhenHeaderNull() throws Exception {
        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void findByIdCodeIs_200() throws Exception {
        Mockito.when(bookingService.findById(anyLong(), anyLong())).thenReturn(responseBookingDto);
        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseBookingDto.getId()), Long.class));
    }

    @Test
    public void findByIdCodeIs_404_WhenSomethingNotFound() throws Exception {
        Mockito.when(bookingService.findById(anyLong(), anyLong())).thenThrow(ObjectNotFoundException.class);
        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    public void findByIdCodeIs_400_WhenHeaderNull() throws Exception {
        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void findAllBookingByBookerCodeIs_200() throws Exception {
        List<ResponseBookingDto> responseBookingsDto = List.of(responseBookingDto);
        Mockito.when(bookingService.findAllBookingByBooker(anyLong(), any(), any())).thenReturn(responseBookingsDto);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(responseBookingsDto.size())))
                .andExpect(jsonPath("$[0].id", is(responseBookingsDto.get(0).getId()), Long.class));
    }

    @Test
    public void findAllBookingByBookerCodeIs_400_WhenParameterValueFromNegative() throws Exception {
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "-2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void findAllBookingByBookerCodeIs_400_WhenHeaderNull() throws Exception {
        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void findAllBookingForOwnerByAllItemsCodeIs_200() throws Exception {
        List<ResponseBookingDto> responseBookingsDto = List.of(responseBookingDto);
        Mockito.when(bookingService.findAllBookingForOwnerByAllItems(anyLong(), any(), any()))
                .thenReturn(responseBookingsDto);

        mvc.perform(get("/bookings/owner", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(responseBookingsDto.size())))
                .andExpect(jsonPath("$[0].id", is(responseBookingsDto.get(0).getId()), Long.class));

    }

    @Test
    public void findAllBookingForOwnerByAllItemsCodeIs_400_WhenParameterValueFromNegative() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "-2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void findAllBookingForOwnerByAllItemsCodeIs_400_WhenHeaderNull() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}