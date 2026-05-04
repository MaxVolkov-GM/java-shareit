package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@UtilityClass
public class ItemRequestMapper {

	public ItemRequestDto toDto(ItemRequest itemRequest, List<Item> items) {
		ItemRequestDto itemRequestDto = new ItemRequestDto();
		itemRequestDto.setId(itemRequest.getId());
		itemRequestDto.setDescription(itemRequest.getDescription());
		itemRequestDto.setCreated(itemRequest.getCreated());
		itemRequestDto.setItems(items.stream()
				.map(ItemRequestMapper::toItemForRequestDto)
				.toList());
		return itemRequestDto;
	}

	private ItemForRequestDto toItemForRequestDto(Item item) {
		return new ItemForRequestDto(
				item.getId(),
				item.getName(),
				item.getOwner().getId()
		);
	}
}