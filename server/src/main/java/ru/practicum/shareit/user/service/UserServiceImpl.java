package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
		try {
			User user = UserMapper.toUser(userDto);
			return UserMapper.toDto(userRepository.save(user));
		} catch (DataIntegrityViolationException e) {
			throw new ConflictException("Email already exists");
		}
	}

	@Override
	public UserDto update(Long userId, UserDto userDto) {
		User existingUser = getUserById(userId);

		if (userDto.getName() != null) {
			existingUser.setName(userDto.getName());
		}

		if (userDto.getEmail() != null) {
			existingUser.setEmail(userDto.getEmail());
		}

		try {
			return UserMapper.toDto(userRepository.save(existingUser));
		} catch (DataIntegrityViolationException e) {
			throw new ConflictException("Email already exists");
		}
	}

	@Override
	public UserDto getById(Long userId) {
		return UserMapper.toDto(getUserById(userId));
	}

	@Override
	public User getUserById(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found"));
	}

	@Override
	public List<UserDto> getAll() {
		return userRepository.findAll().stream()
				.map(UserMapper::toDto)
				.toList();
	}

	@Override
	public void delete(Long userId) {
		userRepository.deleteById(userId);
	}
}