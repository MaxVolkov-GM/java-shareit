package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceValidationTest {

	@Autowired
	private ItemRequestService itemRequestService;

	@Autowired
	private UserService userService;

	@Test
	void create_whenUserDoesNotExist_thenThrowsNotFoundException() {
		ItemRequestDto dto = new ItemRequestDto(
				null,
				"Need drill",
				null,
				null,
				List.of()
		);

		assertThrows(NotFoundException.class, () -> itemRequestService.create(99999L, dto));
	}

	@Test
	void create_whenValidData_thenReturnsRequest() {
		User user = userService.create(new User(null, "User", "request-validation-create@mail.com"));

		ItemRequestDto dto = new ItemRequestDto(
				null,
				"Need drill",
				null,
				LocalDateTime.now(),
				List.of()
		);

		ItemRequestDto created = itemRequestService.create(user.getId(), dto);

		assertNotNull(created);
		assertNotNull(created.getId());
	}

	@Test
	void getUserRequests_whenUserDoesNotExist_thenThrowsNotFoundException() {
		assertThrows(NotFoundException.class, () -> itemRequestService.getUserRequests(99999L));
	}

	@Test
	void getAllRequests_whenUserDoesNotExist_thenThrowsNotFoundException() {
		assertThrows(NotFoundException.class, () -> itemRequestService.getAllRequests(99999L));
	}

	@Test
	void getById_whenUserDoesNotExist_thenThrowsNotFoundException() {
		assertThrows(NotFoundException.class, () -> itemRequestService.getById(99999L, 1L));
	}

	@Test
	void getById_whenRequestDoesNotExist_thenThrowsNotFoundException() {
		User user = userService.create(new User(null, "User", "request-validation-get-missing@mail.com"));

		assertThrows(NotFoundException.class, () -> itemRequestService.getById(user.getId(), 99999L));
	}

	@Test
	void getUserRequests_whenUserHasNoRequests_thenReturnsEmptyList() {
		User user = userService.create(new User(null, "User", "request-validation-empty-own@mail.com"));

		assertTrue(itemRequestService.getUserRequests(user.getId()).isEmpty());
	}

	@Test
	void getAllRequests_shouldNotReturnCurrentUserRequests() {
		User user = userService.create(new User(null, "User", "request-validation-empty-all@mail.com"));

		ItemRequestDto dto = new ItemRequestDto(
				null,
				"Need drill",
				null,
				LocalDateTime.now(),
				List.of()
		);

		ItemRequestDto created = itemRequestService.create(user.getId(), dto);

		List<ItemRequestDto> requests = itemRequestService.getAllRequests(user.getId());

		assertFalse(requests.stream()
				.anyMatch(request -> request.getId().equals(created.getId())));
	}

	@Test
	void getAllRequests_shouldReturnOtherUserRequests() {
		User currentUser = userService.create(new User(null, "Current", "request-validation-current@mail.com"));
		User otherUser = userService.create(new User(null, "Other", "request-validation-other@mail.com"));

		ItemRequestDto dto = new ItemRequestDto(
				null,
				"Need drill",
				null,
				LocalDateTime.now(),
				List.of()
		);

		ItemRequestDto otherRequest = itemRequestService.create(otherUser.getId(), dto);

		List<ItemRequestDto> requests = itemRequestService.getAllRequests(currentUser.getId());

		assertTrue(requests.stream()
				.anyMatch(request -> request.getId().equals(otherRequest.getId())));
	}
}