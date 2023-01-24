package ru.practicum.shareit.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.client.CommentClient;
import ru.practicum.shareit.comment.dto.CommentDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@Validated
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentClient commentClient;

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> create(@RequestBody @Valid CommentDto commentDto,
                                         @PathVariable("itemId") long itemId,
                                         @RequestHeader("X-Sharer-User-Id") @NotNull long userId) {
        log.debug("Получен запрос POST на создание комментария от пользователя - {}", userId);
        return commentClient.create("/" + itemId + "/comment", userId, commentDto);
    }

}
