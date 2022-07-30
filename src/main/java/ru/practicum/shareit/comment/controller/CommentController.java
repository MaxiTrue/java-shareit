package ru.practicum.shareit.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@Validated
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{itemId}/comment")
    public CommentDto create(@RequestBody @Valid CommentDto commentDto,
                             @PathVariable("itemId") long itemId,
                             @RequestHeader("X-Sharer-User-Id") @NotNull long userId)
            throws ObjectNotFoundException, ValidException {
        log.debug("Получен запрос POST на создание комментария от пользователя - {}", userId);
        return commentService.create(commentDto, itemId, userId);
    }

}
