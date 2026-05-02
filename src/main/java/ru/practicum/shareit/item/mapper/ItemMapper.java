package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class ItemMapper {

	public Item toItem(ItemDto dto) {
		Item item = new Item();
		item.setId(dto.getId());
		item.setName(dto.getName());
		item.setDescription(dto.getDescription());
		item.setAvailable(dto.getAvailable());
		return item;
	}

	public ItemDto toDto(Item item) {
		ItemDto dto = new ItemDto();
		dto.setId(item.getId());
		dto.setName(item.getName());
		dto.setDescription(item.getDescription());
		dto.setAvailable(item.getAvailable());
		return dto;
	}
}