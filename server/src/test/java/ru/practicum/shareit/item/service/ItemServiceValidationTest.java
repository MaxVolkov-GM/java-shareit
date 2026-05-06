package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceValidationTest {

	@Autowired
	private ItemService itemService;

	@Autowired
	private UserService userService;

	@Test
	void getById_whenItemDoesNotExist_thenThrowsNotFoundException() {
		User user = userService.create(new User(null, "User", "item-validation-user-not-found@mail.com"));

		assertThrows(NotFoundException.class, () -> itemService.getById(99999L, user.getId()));
	}

	@Test
	void update_whenUserIsNotOwner_thenThrowsNotFoundException() {
		User owner = userService.create(new User(null, "Owner", "item-validation-owner-update@mail.com"));
		User other = userService.create(new User(null, "Other", "item-validation-other-update@mail.com"));
		Item item = itemService.create(
				new Item(null, "Drill", "Description", true, owner, null),
				owner.getId()
		);

		Item update = new Item();
		update.setName("Updated");

		assertThrows(NotFoundException.class, () -> itemService.update(item.getId(), update, other.getId()));
	}

	@Test
	void create_whenUserDoesNotExist_thenThrowsNotFoundException() {
		Item item = new Item(null, "Drill", "Description", true, null, null);

		assertThrows(NotFoundException.class, () -> itemService.create(item, 99999L));
	}

	@Test
	void getAllByUser_whenUserDoesNotExist_thenThrowsNotFoundException() {
		assertThrows(NotFoundException.class, () -> itemService.getAllByUser(99999L));
	}

	@Test
	void addComment_whenUserDoesNotHaveCompletedBooking_thenThrowsValidationException() {
		User owner = userService.create(new User(null, "Owner", "item-validation-owner-comment@mail.com"));
		User author = userService.create(new User(null, "Author", "item-validation-author-comment@mail.com"));
		Item item = itemService.create(
				new Item(null, "Drill", "Description", true, owner, null),
				owner.getId()
		);

		assertThrows(ValidationException.class, () -> itemService.addComment(item.getId(), author.getId(), "Good item"));
	}

	@Test
	void addComment_whenItemDoesNotExist_thenThrowsNotFoundException() {
		User author = userService.create(new User(null, "Author", "item-validation-author-no-item@mail.com"));

		assertThrows(NotFoundException.class, () -> itemService.addComment(99999L, author.getId(), "Good item"));
	}

	@Test
	void addComment_whenUserDoesNotExist_thenThrowsNotFoundException() {
		User owner = userService.create(new User(null, "Owner", "item-validation-owner-no-author@mail.com"));
		Item item = itemService.create(
				new Item(null, "Drill", "Description", true, owner, null),
				owner.getId()
		);

		assertThrows(NotFoundException.class, () -> itemService.addComment(item.getId(), 99999L, "Good item"));
	}

	@Test
	void search_whenTextIsBlank_thenReturnsEmptyList() {
		assertTrue(itemService.search("").isEmpty());
	}

	@Test
	void search_whenTextIsNull_thenReturnsEmptyList() {
		assertTrue(itemService.search(null).isEmpty());
	}

	@Test
	void create_whenValidData_thenReturnsItem() {
		User owner = userService.create(new User(null, "Owner", "item-validation-owner-create@mail.com"));
		Item item = itemService.create(
				new Item(null, "Drill", "Description", true, null, null),
				owner.getId()
		);

		assertNotNull(item);
		assertNotNull(item.getId());
	}

	@Test
	void addComment_whenValidationFails_thenDoesNotCreateComment() {
		User owner = userService.create(new User(null, "Owner", "item-validation-owner-no-comment@mail.com"));
		User author = userService.create(new User(null, "Author", "item-validation-author-no-comment@mail.com"));
		Item item = itemService.create(
				new Item(null, "Drill", "Description", true, owner, null),
				owner.getId()
		);

		assertThrows(ValidationException.class, () -> {
			Comment comment = itemService.addComment(item.getId(), author.getId(), "Good item");
			assertNotNull(comment);
		});
	}
}