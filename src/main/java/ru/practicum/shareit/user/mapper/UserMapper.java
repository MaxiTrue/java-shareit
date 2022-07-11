package ru.practicum.shareit.user.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final UserStorage userStorage;

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail()).build();
    }

    public User toUserEntity(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail()).build();
    }

    public UserDto toUserDtoFromPartialUpdate(UserDto userDto, long userId) throws Throwable {
        User user = userStorage.getById(userId)
                .orElseThrow((Supplier<Throwable>) () -> new ObjectNotFoundException("пользователь", userId));

        UserDto copyUserDto = userDto;
        copyUserDto.setId(user.getId());
        if (copyUserDto.getName() == null) copyUserDto.setName(user.getName());
        if (copyUserDto.getEmail() == null) copyUserDto.setEmail(user.getEmail());

        return copyUserDto;
    }

    public Collection<UserDto> toListResponseUserDto(Collection<User> users) {
        return users.stream().map(this::toUserDto).collect(Collectors.toList());
    }

}
