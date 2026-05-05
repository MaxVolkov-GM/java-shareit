package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

	private final ItemService itemService;
	private final BookingRepository bookingRepository;
	private final CommentRepository commentRepository;

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
		Item item = itemService.getById(itemId, userId);
		List<Comment> comments = commentRepository.findByItemIdOrderByCreatedAsc(itemId);

		Booking lastBooking = null;
		Booking nextBooking = null;

		if (item.getOwner().getId().equals(userId)) {
			LocalDateTime now = LocalDateTime.now();

			lastBooking = bookingRepository
					.findFirstByItemIdAndStatusAndEndBeforeOrderByStartDesc(
							itemId, BookingStatus.APPROVED, now);

			nextBooking = bookingRepository
					.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
							itemId, BookingStatus.APPROVED, now);
		}

		return ItemMapper.toDto(item, lastBooking, nextBooking, comments);
	}

	@GetMapping
	public List<ItemDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
		List<Item> items = itemService.getAllByUser(userId);
		List<Long> itemIds = items.stream().map(Item::getId).toList();

		Map<Long, List<Comment>> commentsByItemId =
				commentRepository.findByItemIdInOrderByCreatedAsc(itemIds)
						.stream()
						.collect(Collectors.groupingBy(c -> c.getItem().getId()));

		return items.stream()
				.map(item -> {
					LocalDateTime now = LocalDateTime.now();

					Booking lastBooking = bookingRepository
							.findFirstByItemIdAndStatusAndEndBeforeOrderByStartDesc(
									item.getId(), BookingStatus.APPROVED, now);

					Booking nextBooking = bookingRepository
							.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
									item.getId(), BookingStatus.APPROVED, now);

					return ItemMapper.toDto(
							item,
							lastBooking,
							nextBooking,
							commentsByItemId.getOrDefault(item.getId(), List.of())
					);
				})
				.toList();
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