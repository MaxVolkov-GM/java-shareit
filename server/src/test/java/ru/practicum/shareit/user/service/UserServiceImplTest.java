package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.user.User;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceImplTest {

	@Autowired
	private UserService userService;

	@Test
	void createUser() {
		User user = new User();
		user.setName("Test");
		user.setEmail("test@mail.com");

		User created = userService.create(user);

		assertThat(created.getId()).isNotNull();
		assertThat(created.getEmail()).isEqualTo("test@mail.com");
	}

	@Test
	void getUserById() {
		User user = new User();
		user.setName("Test");
		user.setEmail("get@mail.com");

		User created = userService.create(user);

		User found = userService.getById(created.getId());

		assertThat(found.getId()).isEqualTo(created.getId());
	}
}