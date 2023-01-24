package ru.practicum.shareit.item.mapper;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto.BookingFromItem;
import ru.practicum.shareit.item.dto.ResponseItemDto.CommentFromItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ItemMapper {

    public Item toEntityItem(ItemDto itemDto, User user, @Nullable ItemRequest itemRequest) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        item.setRequest(itemRequest);
        return item;
    }

    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null).build();
    }

    public List<ItemDto> toListItemDto(Collection<Item> items) {
        return items.stream().map(this::toItemDto).collect(Collectors.toList());
    }


    public ResponseItemDto toResponseItemDto(Item item,
                                             @Nullable Booking bookingLast,
                                             @Nullable Booking bookingNext,
                                             Set<Comment> comments) {

        BookingFromItem lastBooking = bookingLast != null ? BookingFromItem.builder()
                .id(bookingLast.getId())
                .bookerId(bookingLast.getBooker().getId()).build() : null;

        BookingFromItem nextBooking = bookingNext != null ? BookingFromItem.builder()
                .id(bookingNext.getId())
                .bookerId(bookingNext.getBooker().getId()).build() : null;

        Set<CommentFromItem> commentsFromItem = new HashSet<>();
        for (Comment comment : comments) {
            CommentFromItem commentFromItem = CommentFromItem.builder()
                    .id(comment.getId())
                    .text(comment.getText())
                    .authorName(comment.getAuthor().getName())
                    .create(comment.getCreated()).build();
            commentsFromItem.add(commentFromItem);
        }

        ResponseItemDto itemDtoFromOwner = new ResponseItemDto();
        itemDtoFromOwner.setId(item.getId());
        itemDtoFromOwner.setName(item.getName());
        itemDtoFromOwner.setDescription(item.getDescription());
        itemDtoFromOwner.setAvailable(item.getAvailable());
        itemDtoFromOwner.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);
        itemDtoFromOwner.setLastBooking(lastBooking);
        itemDtoFromOwner.setNextBooking(nextBooking);
        itemDtoFromOwner.setComments(commentsFromItem);
        return itemDtoFromOwner;
    }

}
