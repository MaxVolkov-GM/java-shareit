package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

	private final ItemRepository itemRepository;
	private final UserService userService;

	@Override
	public ItemDto create(ItemDto itemDto, Long userId) {
		User owner = userService.getUserById(userId);

		Item item = ItemMapper.toItem(itemDto);
		item.setOwner(owner);

		return ItemMapper.toDto(itemRepository.save(item));
	}

	@Override
	public ItemDto update(Long itemId, ItemDto itemDto, Long userId) {
		Item existing = getItemOrThrow(itemId);

		if (!existing.getOwner().getId().equals(userId)) {
			throw new NotFoundException("Only owner can update item");
		}

		if (itemDto.getName() != null) {
			existing.setName(itemDto.getName());
		}

		if (itemDto.getDescription() != null) {
			existing.setDescription(itemDto.getDescription());
		}

		if (itemDto.getAvailable() != null) {
			existing.setAvailable(itemDto.getAvailable());
		}

		return ItemMapper.toDto(itemRepository.save(existing));
	}

	@Override
	public ItemDto getById(Long itemId, Long userId) {
		return ItemMapper.toDto(getItemOrThrow(itemId));
	}

	@Override
	public List<ItemDto> getAllByUser(Long userId) {
		userService.getUserById(userId);

		return itemRepository.findAll().stream()
				.filter(item -> item.getOwner().getId().equals(userId))
				.map(ItemMapper::toDto)
				.toList();
	}

	@Override
	public List<ItemDto> search(String text) {
		if (text == null || text.isBlank()) {
			return List.of();
		}

		return itemRepository.search(text).stream()
				.map(ItemMapper::toDto)
				.toList();
	}

	private Item getItemOrThrow(Long itemId) {
		Item item = itemRepository.findById(itemId);

		if (item == null) {
			throw new NotFoundException("Item not found");
		}

		return item;
	}
}