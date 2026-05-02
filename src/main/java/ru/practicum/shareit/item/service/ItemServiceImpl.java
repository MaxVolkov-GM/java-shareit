package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemServiceImpl implements ItemService {

	private final Map<Long, Item> items = new HashMap<>();
	private long idCounter = 1;

	@Override
	public Item create(Item item, Long userId) {
		item.setId(idCounter++);
		item.setOwner(new User(userId, null, null));
		items.put(item.getId(), item);
		return item;
	}

	@Override
	public Item update(Long itemId, Item item, Long userId) {
		Item existing = items.get(itemId);

		if (!existing.getOwner().getId().equals(userId)) {
			throw new RuntimeException("Only owner can update item");
		}

		if (item.getName() != null) {
			existing.setName(item.getName());
		}

		if (item.getDescription() != null) {
			existing.setDescription(item.getDescription());
		}

		if (item.getAvailable() != null) {
			existing.setAvailable(item.getAvailable());
		}

		return existing;
	}

	@Override
	public Item getById(Long itemId) {
		return items.get(itemId);
	}

	@Override
	public List<Item> getAllByUser(Long userId) {
		return items.values().stream()
				.filter(item -> item.getOwner().getId().equals(userId))
				.toList();
	}

	@Override
	public List<Item> search(String text) {
		if (text == null || text.isBlank()) {
			return List.of();
		}

		return items.values().stream()
				.filter(Item::getAvailable)
				.filter(item ->
						item.getName().toLowerCase().contains(text.toLowerCase()) ||
								item.getDescription().toLowerCase().contains(text.toLowerCase())
				)
				.toList();
	}
}