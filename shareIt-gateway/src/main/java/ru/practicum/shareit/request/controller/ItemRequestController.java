package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid ItemRequestDto itemRequestDto,
                                         @RequestHeader("X-Sharer-User-Id") @NotNull long userId) {
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByRequesterId(@RequestHeader("X-Sharer-User-Id") @NotNull long userId) {
        return itemRequestClient.findAllByRequesterId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                          @Positive @RequestParam(name = "size", defaultValue = "5") int size,
                                          @RequestHeader("X-Sharer-User-Id") @NotNull long userId) {
        return itemRequestClient.findAll("/all", userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@PathVariable("requestId") long requestId,
                                           @RequestHeader("X-Sharer-User-Id") @NotNull long userId) {
        return itemRequestClient.findById(requestId, userId);
    }

}
