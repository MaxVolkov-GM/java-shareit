package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRequest;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ItemRequestServiceImplTest {

	@Autowired
	private ItemRequestService itemRequestService;

	@Autowired
	private ItemService itemService;

	@Autowired
	private UserService userService;

	@Test
	void createRequest() {
		User user = userService.create(new User(null, "Requester", "request-create@mail.com"));

		ItemRequestDto requestDto = new ItemRequestDto();
		requestDto.setDescription("Нужна дрель");

		ItemRequestDto created = itemRequestService.create(user.getId(), requestDto);

		assertThat(created.getId()).isNotNull();
		assertThat(created.getDescription()).isEqualTo("Нужна дрель");
		assertThat(created.getRequestorId()).isEqualTo(user.getId());
		assertThat(created.getCreated()).isNotNull();
		assertThat(created.getItems()).isEmpty();
	}

	@Test
	void getUserRequests() {
		User user = userService.create(new User(null, "Requester", "request-user-list@mail.com"));

		ItemRequestDto requestDto = new ItemRequestDto();
		requestDto.setDescription("Нужен шуруповерт");

		ItemRequestDto created = itemRequestService.create(user.getId(), requestDto);

		List<ItemRequestDto> requests = itemRequestService.getUserRequests(user.getId());

		assertThat(requests).extracting(ItemRequestDto::getId).contains(created.getId());
	}

	@Test
	void getRequestByIdWithItems() {
		User requester = userService.create(new User(null, "Requester", "request-by-id-requester@mail.com"));
		User owner = userService.create(new User(null, "Owner", "request-by-id-owner@mail.com"));

		ItemRequestDto requestDto = new ItemRequestDto();
		requestDto.setDescription("Нужен инструмент");

		ItemRequestDto createdRequest = itemRequestService.create(requester.getId(), requestDto);

		ItemRequest request = new ItemRequest();
		request.setId(createdRequest.getId());

		Item item = new Item();
		item.setName("Дрель");
		item.setDescription("Описание");
		item.setAvailable(true);
		item.setRequest(request);

		Item createdItem = itemService.create(item, owner.getId());

		ItemRequestDto found = itemRequestService.getById(requester.getId(), createdRequest.getId());

		assertThat(found.getId()).isEqualTo(createdRequest.getId());
		assertThat(found.getItems()).hasSize(1);
		assertThat(found.getItems().getFirst().getId()).isEqualTo(createdItem.getId());
		assertThat(found.getItems().getFirst().getOwnerId()).isEqualTo(owner.getId());
	}
}