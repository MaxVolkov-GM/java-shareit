package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
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
import ru.practicum.shareit.item.model.ItemRequest;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

	private final ItemRepository itemRepository;
	private final CommentRepository commentRepository;
	private final BookingRepository bookingRepository;
	private final ItemRequestRepository requestRepository;
	private final UserService userService;

	@Override
	public ItemDto create(ItemDto itemDto, Long userId) {
		User owner = userService.getUserById(userId);

		Item item = ItemMapper.toItem(itemDto);
		item.setOwner(owner);

		if (itemDto.getRequestId() != null) {
			ItemRequest request = requestRepository.findById(itemDto.getRequestId())
					.orElseThrow(() -> new NotFoundException("Request not found"));
			item.setRequest(request);
		}

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

		List<Item> items = itemRepository.findByOwnerId(userId);
		List<Long> itemIds = items.stream()
				.map(Item::getId)
				.toList();

		if (itemIds.isEmpty()) {
			return List.of();
		}

		LocalDateTime now = LocalDateTime.now();

		Map<Long, List<CommentDto>> commentsByItemId = commentRepository.findByItemIdInOrderByCreatedDesc(itemIds)
				.stream()
				.collect(Collectors.groupingBy(
						comment -> comment.getItem().getId(),
						Collectors.mapping(CommentMapper::toDto, Collectors.toList())
				));

		Map<Long, Booking> lastBookingsByItemId = bookingRepository.findAllByItemIdInAndStatusAndEndBefore(
						itemIds,
						BookingStatus.APPROVED,
						now,
						Sort.by(Sort.Direction.DESC, "end")
				)
				.stream()
				.collect(Collectors.toMap(
						booking -> booking.getItem().getId(),
						Function.identity(),
						(first, second) -> first
				));

		Map<Long, Booking> nextBookingsByItemId = bookingRepository.findAllByItemIdInAndStatusAndStartAfter(
						itemIds,
						BookingStatus.APPROVED,
						now,
						Sort.by(Sort.Direction.ASC, "start")
				)
				.stream()
				.collect(Collectors.toMap(
						booking -> booking.getItem().getId(),
						Function.identity(),
						(first, second) -> first
				));

		return items.stream()
				.map(item -> {
					ItemDto itemDto = ItemMapper.toDto(item);
					itemDto.setComments(commentsByItemId.getOrDefault(item.getId(), List.of()));
					itemDto.setLastBooking(ItemMapper.toBookingShortDto(lastBookingsByItemId.get(item.getId())));
					itemDto.setNextBooking(ItemMapper.toBookingShortDto(nextBookingsByItemId.get(item.getId())));
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

		Booking lastBooking = bookingRepository.findFirstByItemIdAndStatusAndEndBefore(
				itemId,
				BookingStatus.APPROVED,
				now,
				Sort.by(Sort.Direction.DESC, "end")
		);

		Booking nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfter(
				itemId,
				BookingStatus.APPROVED,
				now,
				Sort.by(Sort.Direction.ASC, "start")
		);

		itemDto.setLastBooking(ItemMapper.toBookingShortDto(lastBooking));
		itemDto.setNextBooking(ItemMapper.toBookingShortDto(nextBooking));
	}

	private Item getItemOrThrow(Long itemId) {
		return itemRepository.findById(itemId)
				.orElseThrow(() -> new NotFoundException("Item not found"));
	}
}