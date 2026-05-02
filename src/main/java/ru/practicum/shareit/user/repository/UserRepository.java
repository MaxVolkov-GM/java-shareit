package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepository {
	private final Map<Long, User> users = new HashMap<>();
	private long idCounter = 1;

	public User save(User user) {
		if (user.getId() == null) {
			user.setId(idCounter++);
		}

		users.put(user.getId(), user);
		return user;
	}

	public User findById(Long userId) {
		return users.get(userId);
	}

	public List<User> findAll() {
		return new ArrayList<>(users.values());
	}

	public void delete(Long userId) {
		users.remove(userId);
	}
}