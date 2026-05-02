package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
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
		checkEmailUnique(user.getEmail(), null);
		user.setId(idCounter++);
		users.put(user.getId(), user);
		return user;
	}

	@Override
	public User update(Long userId, User user) {
		User existingUser = users.get(userId);

		if (user.getName() != null) {
			existingUser.setName(user.getName());
		}

		if (user.getEmail() != null) {
			checkEmailUnique(user.getEmail(), userId);
			existingUser.setEmail(user.getEmail());
		}

		return existingUser;
	}

	@Override
	public User getById(Long userId) {
		return users.get(userId);
	}

	@Override
	public List<User> getAll() {
		return new ArrayList<>(users.values());
	}

	@Override
	public void delete(Long userId) {
		users.remove(userId);
	}

	private void checkEmailUnique(String email, Long currentUserId) {
		boolean emailExists = users.values().stream()
				.anyMatch(user -> user.getEmail().equals(email)
						&& !user.getId().equals(currentUserId));

		if (emailExists) {
			throw new RuntimeException("Email already exists");
		}
	}
}