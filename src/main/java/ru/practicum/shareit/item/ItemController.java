package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

	private final ItemService itemService;

	@PostMapping
	public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
	                      @Valid @RequestBody ItemDto itemDto) {
		return itemService.create(itemDto, userId);
	}

	@PatchMapping("/{itemId}")
	public ItemDto update(@PathVariable Long itemId,
	                      @RequestHeader("X-Sharer-User-Id") Long userId,
	                      @RequestBody ItemDto itemDto) {
		return itemService.update(itemId, itemDto, userId);
	}

	@GetMapping("/{itemId}")
	public ItemDto getById(@PathVariable Long itemId,
	                       @RequestHeader("X-Sharer-User-Id") Long userId) {
		return itemService.getById(itemId, userId);
	}

	@GetMapping
	public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
		return itemService.getAllByUser(userId);
	}

	@GetMapping("/search")
	public List<ItemDto> search(@RequestParam String text) {
		return itemService.search(text);
	}
}