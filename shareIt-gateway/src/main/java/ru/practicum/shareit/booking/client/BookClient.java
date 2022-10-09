package ru.practicum.shareit.booking.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.baseclient.BaseClient;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.StateBooking;

import java.util.Map;

@Component
public class BookClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    public BookClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new).build());
    }

    public ResponseEntity<Object> create(long userId, BookingDto bookingDto) {
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Старт бронирования не может быть раньше окончания");
        }
        return post("", userId, bookingDto);
    }

    public ResponseEntity<Object> updateStatus(long bookingId, boolean approved, long userId) {
        Map<String, Object> parameters = Map.of("approved", approved);
        return patch("/" + bookingId + "?approved={approved}", userId, parameters, null);
    }

    public ResponseEntity<Object> findById(long bookingId, long userId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> findAllBookingByBooker(long userId, StateBooking state, int from, int size) {
        Map<String, Object> parameters = Map.of("state", state, "from", from, "size", size);
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> findAllBookingForOwnerByAllItems(String path, long userId, StateBooking state,
                                                                   int from, int size) {
        Map<String, Object> parameters = Map.of("state", state, "from", from, "size", size);
        return get(path + "?state={state}&from={from}&size={size}", userId, parameters);
    }
}

