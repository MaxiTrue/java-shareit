package ru.practicum.shareit.requests.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestWithItemDto;
import ru.practicum.shareit.requests.service.ItemRequestService;

import java.util.Collection;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestBody ItemRequestDto itemRequestDto,
                                 @RequestHeader("X-Sharer-User-Id") long userId)
            throws ObjectNotFoundException {
        return itemRequestService.create(itemRequestDto, userId);
    }

    @GetMapping
    public Collection<ItemRequestWithItemDto> findAllByRequesterId(
            @RequestHeader("X-Sharer-User-Id") long userId) throws ObjectNotFoundException {
        return itemRequestService.findAllByRequesterId(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestWithItemDto> findAll(
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestHeader("X-Sharer-User-Id") long userId) throws ObjectNotFoundException {
        Pageable pageable = PageRequest.of(from / size, size);
        return itemRequestService.findAll(userId, pageable);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemDto findById(
            @PathVariable("requestId") long requestId,
            @RequestHeader("X-Sharer-User-Id") long userId) throws ObjectNotFoundException {
        return itemRequestService.findById(requestId, userId);
    }

}
