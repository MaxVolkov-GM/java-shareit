package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;

	@Override
	public UserDto create(UserDto userDto) {
		checkEmailUnique(userDto.getEmail(), null);

		User user = UserMapper.toUser(userDto);
		return UserMapper.toDto(userRepository.save(user));
	}

	@Override
	public UserDto update(Long userId, UserDto userDto) {
		User existingUser = getUserById(userId);

		if (userDto.getName() != null) {
			existingUser.setName(userDto.getName());
		}

		if (userDto.getEmail() != null) {
			checkEmailUnique(userDto.getEmail(), userId);
			existingUser.setEmail(userDto.getEmail());
		}

		return UserMapper.toDto(userRepository.save(existingUser));
	}

	@Override
	public UserDto getById(Long userId) {
		return UserMapper.toDto(getUserById(userId));
	}

	@Override
	public User getUserById(Long userId) {
		User user = userRepository.findById(userId);

		if (user == null) {
			throw new NotFoundException("User not found");
		}

		return user;
	}

	@Override
	public List<UserDto> getAll() {
		return userRepository.findAll().stream()
				.map(UserMapper::toDto)
				.toList();
	}

	@Override
	public void delete(Long userId) {
		userRepository.delete(userId);
	}

	private void checkEmailUnique(String email, Long currentUserId) {
		boolean emailExists = userRepository.findAll().stream()
				.anyMatch(user -> user.getEmail() != null
						&& user.getEmail().equals(email)
						&& !user.getId().equals(currentUserId));

		if (emailExists) {
			throw new ConflictException("Email already exists");
		}
	}
}