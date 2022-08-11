package ru.practicum.shareit.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StateBooking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.storage.CommentStorage;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentStorage commentStorage;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final CommentMapper commentMapper;

    @Override
    public CommentDto create(CommentDto commentDto, long itemId, long authorId)
            throws ObjectNotFoundException, ValidException {
        LocalDateTime now = LocalDateTime.now();
        Item item = itemStorage.findById(itemId).orElseThrow(() -> new ObjectNotFoundException("вещь", itemId));
        User author = userStorage.findById(authorId).orElseThrow(() -> new ObjectNotFoundException("вещь", itemId));
        List<Booking> bookings = bookingStorage.findAllByItemIdAndBookerIdAndStatusAndEndBeforeOrderByEndDesc(
                itemId, authorId, StateBooking.APPROVED, now);
        if (bookings.size() == 0) {
            throw new ValidException("Пользователь id - " + authorId + " не бронировал вещь id - " + itemId);
        }
        Comment comment = commentMapper.toEntityComment(commentDto, item, author);
        return commentMapper.toCommentDto(commentStorage.save(comment));
    }
}
