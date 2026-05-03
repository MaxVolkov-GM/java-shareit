package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

	private final ItemRepository itemRepository;
	private final CommentRepository commentRepository;
	private final BookingRepository bookingRepository;
	private final UserService userService;

	@Override
	public ItemDto create(ItemDto itemDto, Long userId) {
		User owner = userService.getUserById(userId);

		Item item = ItemMapper.toItem(itemDto);
		item.setOwner(owner);

		return ItemMapper.toDto(itemRepository.save(item));
	}

	@Override
	public ItemDto update(Long itemId, ItemDto itemDto, Long userId) {
		Item existing = getItemOrThrow(itemId);

		if (!existing.getOwner().getId().equals(userId)) {
			throw new NotFoundException("Only owner can update item");
		}

		if (itemDto.getName() != null) {
			existing.setName(itemDto.getName());
		}

		if (itemDto.getDescription() != null) {
			existing.setDescription(itemDto.getDescription());
		}

		if (itemDto.getAvailable() != null) {
			existing.setAvailable(itemDto.getAvailable());
		}

		return ItemMapper.toDto(itemRepository.save(existing));
	}

	@Override
	public ItemDto getById(Long itemId, Long userId) {
		userService.getUserById(userId);

		Item item = getItemOrThrow(itemId);
		ItemDto itemDto = ItemMapper.toDto(item);

		addComments(itemDto, itemId);

		if (item.getOwner().getId().equals(userId)) {
			addBookings(itemDto, itemId);
		}

		return itemDto;
	}

	@Override
	public List<ItemDto> getAllByUser(Long userId) {
		userService.getUserById(userId);

		return itemRepository.findByOwnerId(userId).stream()
				.map(item -> {
					ItemDto itemDto = ItemMapper.toDto(item);
					addComments(itemDto, item.getId());
					addBookings(itemDto, item.getId());
					return itemDto;
				})
				.toList();
	}

	@Override
	public List<ItemDto> search(String text) {
		if (text == null || text.isBlank()) {
			return List.of();
		}

		return itemRepository.search(text).stream()
				.map(ItemMapper::toDto)
				.toList();
	}

	@Override
	public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
		User author = userService.getUserById(userId);
		Item item = getItemOrThrow(itemId);

		boolean hasApprovedPastBooking = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
				itemId,
				userId,
				BookingStatus.APPROVED,
				LocalDateTime.now()
		);

		if (!hasApprovedPastBooking) {
			throw new ValidationException("User has not completed booking for this item");
		}

		Comment comment = new Comment();
		comment.setText(commentDto.getText());
		comment.setItem(item);
		comment.setAuthor(author);
		comment.setCreated(LocalDateTime.now());

		return CommentMapper.toDto(commentRepository.save(comment));
	}

	private void addComments(ItemDto itemDto, Long itemId) {
		itemDto.setComments(commentRepository.findByItemIdOrderByCreatedDesc(itemId).stream()
				.map(CommentMapper::toDto)
				.toList());
	}

	private void addBookings(ItemDto itemDto, Long itemId) {
		LocalDateTime now = LocalDateTime.now();

		Booking lastBooking = bookingRepository.findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(
				itemId,
				BookingStatus.APPROVED,
				now
		);

		Booking nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
				itemId,
				BookingStatus.APPROVED,
				now
		);

		itemDto.setLastBooking(ItemMapper.toBookingShortDto(lastBooking));
		itemDto.setNextBooking(ItemMapper.toBookingShortDto(nextBooking));
	}

	private Item getItemOrThrow(Long itemId) {
		return itemRepository.findById(itemId)
				.orElseThrow(() -> new NotFoundException("Item not found"));
	}
}