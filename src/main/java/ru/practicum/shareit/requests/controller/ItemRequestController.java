package ru.practicum.shareit.requests.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestWithItemDto;
import ru.practicum.shareit.requests.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestBody @Valid ItemRequestDto itemRequestDto,
                                 @RequestHeader("X-Sharer-User-Id") @NotNull long userId)
            throws ObjectNotFoundException {
        return itemRequestService.create(itemRequestDto, userId);
    }

    @GetMapping
    public Collection<ItemRequestWithItemDto> findAllByRequesterId(
            @RequestHeader("X-Sharer-User-Id") @NotNull long userId) throws ObjectNotFoundException {
        return itemRequestService.findAllByRequesterId(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestWithItemDto> findAll(
            @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
            @RequestParam(name = "size", defaultValue = "5") @Min(1) int size,
            @RequestHeader("X-Sharer-User-Id") @NotNull long userId) throws ObjectNotFoundException {
        Pageable pageable = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "created"));
        return itemRequestService.findAll(userId, pageable);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemDto findById(
            @PathVariable("requestId") long requestId,
            @RequestHeader("X-Sharer-User-Id") @NotNull long userId) throws ObjectNotFoundException {
        return itemRequestService.findById(requestId, userId);
    }

}
