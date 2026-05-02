package ru.practicum.shareit.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

@UtilityClass
public class UserMapper {

	public UserDto toDto(User user) {
		return new UserDto(
				user.getId(),
				user.getName(),
				user.getEmail()
		);
	}

	public User toUser(UserDto dto) {
		return new User(
				dto.getId(),
				dto.getName(),
				dto.getEmail()
		);
	}
}