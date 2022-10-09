package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private long id;
    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    @Size(groups = {Create.class, Update.class}, min = 3, max = 50)
    private String name;
    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    @Size(groups = {Create.class, Update.class}, max = 300)
    private String description;
    @NotNull(groups = {Create.class})
    private Boolean available;
    private Long requestId;
}
