package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.Create;

import java.util.Collections;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

	private final ItemClient itemClient;

	@PostMapping
	public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                     @Validated(Create.class) @RequestBody ItemDto itemDto) {
		return itemClient.create(userId, itemDto);
	}

	@PatchMapping("/{itemId}")
	public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                     @PathVariable Long itemId,
	                                     @RequestBody ItemDto itemDto) {
		return itemClient.update(userId, itemId, itemDto);
	}

	@GetMapping("/{itemId}")
	public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                      @PathVariable Long itemId) {
		return itemClient.getById(userId, itemId);
	}

	@GetMapping
	public ResponseEntity<Object> getUserItems(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                           @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
	                                           @Positive @RequestParam(defaultValue = "10") Integer size) {
		return itemClient.getUserItems(userId, from, size);
	}

	@GetMapping("/search")
	public ResponseEntity<Object> search(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                     @RequestParam String text,
	                                     @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
	                                     @Positive @RequestParam(defaultValue = "10") Integer size) {
		if (text == null || text.isBlank()) {
			return ResponseEntity.ok(Collections.emptyList());
		}

		return itemClient.search(userId, text, from, size);
	}

	@PostMapping("/{itemId}/comment")
	public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                         @PathVariable Long itemId,
	                                         @Valid @RequestBody CommentDto commentDto) {
		return itemClient.addComment(userId, itemId, commentDto);
	}
}