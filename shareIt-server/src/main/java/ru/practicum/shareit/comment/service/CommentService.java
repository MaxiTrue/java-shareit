package ru.practicum.shareit.comment.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidException;

public interface CommentService {
    CommentDto create(CommentDto commentDto, long itemId, long authorId) throws ObjectNotFoundException, ValidException;
}
