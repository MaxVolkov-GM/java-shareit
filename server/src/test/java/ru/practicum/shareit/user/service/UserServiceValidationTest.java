package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceValidationTest {

	@Autowired
	private UserService userService;

	@Test
	void create_whenEmailAlreadyExists_thenThrowsIllegalStateException() {
		userService.create(new User(null, "User", "user-validation-duplicate@mail.com"));

		User duplicate = new User(null, "Other", "user-validation-duplicate@mail.com");

		assertThrows(IllegalStateException.class, () -> userService.create(duplicate));
	}

	@Test
	void update_whenUserDoesNotExist_thenThrowsNotFoundException() {
		User update = new User(null, "Updated", "updated@mail.com");

		assertThrows(NotFoundException.class, () -> userService.update(99999L, update));
	}

	@Test
	void update_whenEmailAlreadyExists_thenThrowsIllegalStateException() {
		User first = userService.create(new User(null, "First", "user-validation-first@mail.com"));
		User second = userService.create(new User(null, "Second", "user-validation-second@mail.com"));

		User update = new User(null, null, second.getEmail());

		assertThrows(IllegalStateException.class, () -> userService.update(first.getId(), update));
	}

	@Test
	void update_whenOnlyNameProvided_thenUpdatesName() {
		User user = userService.create(new User(null, "Old", "user-validation-name@mail.com"));

		User update = new User(null, "New", null);
		User updated = userService.update(user.getId(), update);

		assertEquals("New", updated.getName());
		assertEquals("user-validation-name@mail.com", updated.getEmail());
	}

	@Test
	void update_whenOnlyEmailProvided_thenUpdatesEmail() {
		User user = userService.create(new User(null, "User", "user-validation-old-email@mail.com"));

		User update = new User(null, null, "user-validation-new-email@mail.com");
		User updated = userService.update(user.getId(), update);

		assertEquals("User", updated.getName());
		assertEquals("user-validation-new-email@mail.com", updated.getEmail());
	}

	@Test
	void getById_whenUserDoesNotExist_thenThrowsNotFoundException() {
		assertThrows(NotFoundException.class, () -> userService.getById(99999L));
	}
}