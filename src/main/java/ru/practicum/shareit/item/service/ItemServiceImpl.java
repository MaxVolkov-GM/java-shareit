package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

	private final UserService userService;
	private final Map<Long, Item> items = new HashMap<>();
	private long idCounter = 1;

	@Override
	public Item create(Item item, Long userId) {
		validateItem(item);

		User owner = userService.getById(userId);

		item.setId(idCounter++);
		item.setOwner(owner);
		items.put(item.getId(), item);

		return item;
	}

	@Override
	public Item update(Long itemId, Item item, Long userId) {
		Item existing = getItemOrThrow(itemId);

		if (!existing.getOwner().getId().equals(userId)) {
			throw new NotFoundException("Only owner can update item");
		}

		if (item.getName() != null) {
			if (item.getName().isBlank()) {
				throw new ValidationException("Name cannot be blank");
			}
			existing.setName(item.getName());
		}

		if (item.getDescription() != null) {
			if (item.getDescription().isBlank()) {
				throw new ValidationException("Description cannot be blank");
			}
			existing.setDescription(item.getDescription());
		}

		if (item.getAvailable() != null) {
			existing.setAvailable(item.getAvailable());
		}

		return existing;
	}

	@Override
	public Item getById(Long itemId) {
		return getItemOrThrow(itemId);
	}

	@Override
	public List<Item> getAllByUser(Long userId) {
		userService.getById(userId);

		return items.values().stream()
				.filter(item -> item.getOwner().getId().equals(userId))
				.toList();
	}

	@Override
	public List<Item> search(String text) {
		if (text == null || text.isBlank()) {
			return List.of();
		}

		String query = text.toLowerCase();

		return items.values().stream()
				.filter(item -> Boolean.TRUE.equals(item.getAvailable()))
				.filter(item ->
						item.getName().toLowerCase().contains(query)
								|| item.getDescription().toLowerCase().contains(query)
				)
				.toList();
	}

	private Item getItemOrThrow(Long itemId) {
		Item item = items.get(itemId);

		if (item == null) {
			throw new NotFoundException("Item not found");
		}

		return item;
	}

	private void validateItem(Item item) {
		if (item.getName() == null || item.getName().isBlank()) {
			throw new ValidationException("Name is required");
		}

		if (item.getDescription() == null || item.getDescription().isBlank()) {
			throw new ValidationException("Description is required");
		}

		if (item.getAvailable() == null) {
			throw new ValidationException("Available is required");
		}
	}
}