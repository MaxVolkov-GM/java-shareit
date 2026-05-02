package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
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
}