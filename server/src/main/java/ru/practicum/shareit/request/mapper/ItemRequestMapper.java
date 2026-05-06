package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemShortDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class ItemRequestMapper {

	public ItemRequest toEntity(ItemRequestDto dto, User requestor) {
		ItemRequest request = new ItemRequest();
		request.setId(dto.getId());
		request.setDescription(dto.getDescription());
		request.setRequestor(requestor);
		request.setCreated(LocalDateTime.now());
		return request;
	}

	public ItemRequestDto toDto(ItemRequest request, List<Item> items) {
		List<ItemShortDto> itemDtos = items.stream()
				.map(ItemRequestMapper::toItemShortDto)
				.toList();

		ItemRequestDto dto = new ItemRequestDto();
		dto.setId(request.getId());
		dto.setDescription(request.getDescription());
		dto.setRequestorId(request.getRequestor().getId());
		dto.setCreated(request.getCreated());
		dto.setItems(itemDtos);
		return dto;
	}

	private ItemShortDto toItemShortDto(Item item) {
		return new ItemShortDto(
				item.getId(),
				item.getName(),
				item.getOwner().getId()
		);
	}
}