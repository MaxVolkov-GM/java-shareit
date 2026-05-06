package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ItemServiceImplTest {

	@Autowired
	private ItemService itemService;

	@Autowired
	private UserService userService;

	@Test
	void createItem() {
		User user = new User();
		user.setName("User");
		user.setEmail("item@test.com");

		User createdUser = userService.create(user);

		Item item = new Item();
		item.setName("Дрель");
		item.setDescription("Описание");
		item.setAvailable(true);

		Item created = itemService.create(item, createdUser.getId());

		assertThat(created.getId()).isNotNull();
		assertThat(created.getName()).isEqualTo("Дрель");
	}

	@Test
	void getUserItems() {
		User user = new User();
		user.setName("User");
		user.setEmail("items@test.com");

		User createdUser = userService.create(user);

		Item item = new Item();
		item.setName("Шуруповерт");
		item.setDescription("Описание");
		item.setAvailable(true);

		itemService.create(item, createdUser.getId());

		List<Item> items = itemService.getAllByUser(createdUser.getId());

		assertThat(items).hasSize(1);
	}
}