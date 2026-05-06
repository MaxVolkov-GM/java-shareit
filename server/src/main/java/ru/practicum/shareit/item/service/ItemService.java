package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

	Item create(Item item, Long userId);

	Item update(Long itemId, Item item, Long userId);

	Item getById(Long itemId);

	Item getById(Long itemId, Long userId);

	ItemDto getDtoById(Long itemId, Long userId);

	List<Item> getAllByUser(Long userId);

	List<ItemDto> getAllDtoByUser(Long userId);

	List<Item> search(String text);

	Comment addComment(Long itemId, Long userId, String text);
}