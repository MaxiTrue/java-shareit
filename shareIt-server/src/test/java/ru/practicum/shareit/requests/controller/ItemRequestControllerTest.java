package ru.practicum.shareit.requests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestWithItemDto;
import ru.practicum.shareit.requests.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    private ItemRequestService itemRequestService;
    @InjectMocks
    private ItemRequestController controller;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private ItemRequestWithItemDto itemRequestWithItemDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(ErrorHandler.class)
                .build();
        ItemRequestWithItemDto.ItemFromItemRequest item = ItemRequestWithItemDto.ItemFromItemRequest.builder()
                .id(1L)
                .name("что - то")
                .description("очень хорошее")
                .available(Boolean.TRUE)
                .requestId(null)
                .ownerId(1L).build();

        itemRequestWithItemDto = ItemRequestWithItemDto.builder()
                .id(1L)
                .description("нужно что - то хорошее")
                .items(Set.of(item)).build();
    }

    @Test
    public void createItemRequestCodeIs_200() throws Exception {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("item").build();
        Mockito.when(itemRequestService.create(any(ItemRequestDto.class), anyLong())).thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    public void createItemRequestWithOutHeaderCodeIs_400() throws Exception {
        mvc.perform(post("/requests", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void findByIdItemRequestCodeIs_200() throws Exception {
        Mockito.when(itemRequestService.findById(anyLong(), anyLong())).thenReturn(itemRequestWithItemDto);

        mvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemRequestWithItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestWithItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestWithItemDto.getDescription())))
                .andExpect(jsonPath("$.items[0].id", is(itemRequestWithItemDto.getItems()
                        .stream().filter(itemFromItemRequest -> itemFromItemRequest.getName().equals("что - то"))
                        .collect(Collectors.toList()).get(0).getId()), Long.class));
    }

    @Test
    public void findByIdItemRequestWithOutHeaderCodeIs_400() throws Exception {
        mvc.perform(get("/requests/{requestId}", 1L)
                        .content(mapper.writeValueAsString(itemRequestWithItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void findAllItemRequestByRequesterIdCodeIs_200() throws Exception {
        Set<ItemRequestWithItemDto> itemRequestsDto = Set.of(itemRequestWithItemDto);
        Mockito.when(itemRequestService.findAllByRequesterId(anyLong())).thenReturn(itemRequestsDto);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemRequestsDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.size()", is(1)));
    }

    @Test
    public void findAllItemRequestByRequesterIdCodeIs_404_NotFoundUser() throws Exception {
        Mockito.when(itemRequestService.findAllByRequesterId(anyLong())).thenThrow(ObjectNotFoundException.class);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        //.content(mapper.writeValueAsString(itemRequestsDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    public void findAllItemRequestCodeIs_200() throws Exception {
        Set<ItemRequestWithItemDto> itemRequestsDto = Set.of(itemRequestWithItemDto);
        Mockito.when(itemRequestService.findAll(anyLong(), any())).thenReturn(itemRequestsDto);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemRequestsDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.size()", is(1)));
    }

    @Test
    public void findAllItemRequestCodeIs_400_NegativeValueArgumentFrom() throws Exception {
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "-10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

}