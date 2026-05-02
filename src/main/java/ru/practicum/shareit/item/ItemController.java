package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

	private final ItemService itemService;

	@PostMapping
	public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
	                      @RequestBody ItemDto itemDto) {
		return ItemMapper.toDto(itemService.create(ItemMapper.toItem(itemDto), userId));
	}

	@PatchMapping("/{itemId}")
	public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
	                      @PathVariable Long itemId,
	                      @RequestBody ItemDto itemDto) {
		return ItemMapper.toDto(itemService.update(itemId, ItemMapper.toItem(itemDto), userId));
	}

	@GetMapping("/{itemId}")
	public ItemDto getById(@PathVariable Long itemId) {
		return ItemMapper.toDto(itemService.getById(itemId));
	}

	@GetMapping
	public List<ItemDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
		return itemService.getAllByUser(userId).stream()
				.map(ItemMapper::toDto)
				.toList();
	}

	@GetMapping("/search")
	public List<ItemDto> search(@RequestParam String text) {
		return itemService.search(text).stream()
				.map(ItemMapper::toDto)
				.toList();
	}
}