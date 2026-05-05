package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	public User create(User user) {
		if (userRepository.findByEmail(user.getEmail()).isPresent()) {
			throw new IllegalStateException("Email already exists");
		}

		return userRepository.save(user);
	}

	@Override
	public User update(Long userId, User user) {
		User existingUser = getById(userId);

		if (user.getEmail() != null) {
			userRepository.findByEmail(user.getEmail())
					.filter(foundUser -> !foundUser.getId().equals(userId))
					.ifPresent(foundUser -> {
						throw new IllegalStateException("Email already exists");
					});

			existingUser.setEmail(user.getEmail());
		}

		if (user.getName() != null) {
			existingUser.setName(user.getName());
		}

		return userRepository.save(existingUser);
	}

	@Override
	@Transactional(readOnly = true)
	public User getById(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> getAll() {
		return userRepository.findAll();
	}

	@Override
	public void delete(Long userId) {
		userRepository.deleteById(userId);
	}
}