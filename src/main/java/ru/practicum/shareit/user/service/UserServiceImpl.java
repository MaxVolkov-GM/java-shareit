package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
	private final Map<Long, User> users = new HashMap<>();
	private long idCounter = 1;

	@Override
	public User create(User user) {
		validateUser(user);
		checkEmailUnique(user.getEmail(), null);
		user.setId(idCounter++);
		users.put(user.getId(), user);
		return user;
	}

	@Override
	public User update(Long userId, User user) {
		User existingUser = getById(userId);

		if (user.getName() != null) {
			existingUser.setName(user.getName());
		}

		if (user.getEmail() != null) {
			validateEmail(user.getEmail());
			checkEmailUnique(user.getEmail(), userId);
			existingUser.setEmail(user.getEmail());
		}

		return existingUser;
	}

	@Override
	public User getById(Long userId) {
		User user = users.get(userId);

		if (user == null) {
			throw new NotFoundException("User not found");
		}

		return user;
	}

	@Override
	public List<User> getAll() {
		return new ArrayList<>(users.values());
	}

	@Override
	public void delete(Long userId) {
		users.remove(userId);
	}

	private void validateUser(User user) {
		if (user.getEmail() == null || user.getEmail().isBlank()) {
			throw new ValidationException("Email is required");
		}

		validateEmail(user.getEmail());
	}

	private void validateEmail(String email) {
		if (!email.contains("@")) {
			throw new ValidationException("Email is invalid");
		}
	}

	private void checkEmailUnique(String email, Long currentUserId) {
		boolean emailExists = users.values().stream()
				.anyMatch(user -> user.getEmail() != null
						&& user.getEmail().equals(email)
						&& !user.getId().equals(currentUserId));

		if (emailExists) {
			throw new ConflictException("Email already exists");
		}
	}
}