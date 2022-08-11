package ru.practicum.shareit.requests.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestWithItemDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.requests.dto.ItemRequestWithItemDto.ItemFromItemRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ItemRequestMapper {

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequester(user);
        itemRequest.setCreated(itemRequestDto.getCreated());
        return itemRequest;
    }

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated()).build();
    }

    //WARNING аргументы item может быть NULL
    public ItemRequestWithItemDto toItemRequestWithItemDto(ItemRequest itemRequest, List<Item> items) {

        Set<ItemFromItemRequest> itemsFromItemRequest = new HashSet<>();
        for (Item item : items) {
            itemsFromItemRequest.add(ItemFromItemRequest.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .description(item.getDescription())
                    .available(item.getAvailable())
                    .requestId(itemRequest.getId())
                    .ownerId(item.getOwner().getId()).build());
        }

        return ItemRequestWithItemDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemsFromItemRequest).build();
    }
}
