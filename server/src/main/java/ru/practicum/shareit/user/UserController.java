package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping
	public UserDto create(@RequestBody UserDto userDto) {
		return UserMapper.toDto(userService.create(UserMapper.toUser(userDto)));
	}

	@PatchMapping("/{userId}")
	public UserDto update(@PathVariable Long userId,
	                      @RequestBody UserDto userDto) {
		return UserMapper.toDto(userService.update(userId, UserMapper.toUser(userDto)));
	}

	@GetMapping("/{userId}")
	public UserDto getById(@PathVariable Long userId) {
		return UserMapper.toDto(userService.getById(userId));
	}

	@GetMapping
	public List<UserDto> getAll() {
		return userService.getAll().stream()
				.map(UserMapper::toDto)
				.collect(Collectors.toList());
	}

	@DeleteMapping("/{userId}")
	public void delete(@PathVariable Long userId) {
		userService.delete(userId);
	}
}