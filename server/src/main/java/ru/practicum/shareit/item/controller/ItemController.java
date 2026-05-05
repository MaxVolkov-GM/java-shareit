package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
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
	public ItemDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
	                       @PathVariable Long itemId) {
		return itemService.getDtoById(itemId, userId);
	}

	@GetMapping
	public List<ItemDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
		return itemService.getAllDtoByUser(userId);
	}

	@GetMapping("/search")
	public List<ItemDto> search(@RequestParam String text) {
		return itemService.search(text).stream()
				.map(ItemMapper::toDto)
				.toList();
	}

	@PostMapping("/{itemId}/comment")
	public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
	                             @PathVariable Long itemId,
	                             @RequestBody CommentDto commentDto) {
		return ItemMapper.toCommentDto(
				itemService.addComment(itemId, userId, commentDto.getText())
		);
	}
}