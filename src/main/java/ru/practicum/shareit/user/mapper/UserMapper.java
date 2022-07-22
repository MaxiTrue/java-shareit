package ru.practicum.shareit.user.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

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

    public UserDto toUserDtoFromPartialUpdate(UserDto userDto, User user) throws Throwable {
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
