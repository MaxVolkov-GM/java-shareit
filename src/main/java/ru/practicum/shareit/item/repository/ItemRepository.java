package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ItemRepository {
	private final Map<Long, Item> items = new HashMap<>();
	private long idCounter = 1;

	public Item save(Item item) {
		if (item.getId() == null) {
			item.setId(idCounter++);
		}

		items.put(item.getId(), item);
		return item;
	}

	public Item findById(Long itemId) {
		return items.get(itemId);
	}

	public List<Item> findAll() {
		return new ArrayList<>(items.values());
	}

	public List<Item> search(String text) {
		String query = text.toLowerCase();

		return items.values().stream()
				.filter(item -> Boolean.TRUE.equals(item.getAvailable()))
				.filter(item ->
						item.getName().toLowerCase().contains(query)
								|| item.getDescription().toLowerCase().contains(query)
				)
				.toList();
	}
}