package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRequest;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

	private static final Sort START_DESC = Sort.by(Sort.Direction.DESC, "start");
	private static final Sort START_ASC = Sort.by(Sort.Direction.ASC, "start");

	private final ItemRepository itemRepository;
	private final UserRepository userRepository;
	private final ItemRequestRepository itemRequestRepository;
	private final BookingRepository bookingRepository;
	private final CommentRepository commentRepository;

	@Override
	public Item create(Item item, Long userId) {
		User owner = userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found"));

		item.setOwner(owner);

		if (item.getRequest() != null && item.getRequest().getId() != null) {
			ItemRequest request = itemRequestRepository.findById(item.getRequest().getId())
					.orElseThrow(() -> new NotFoundException("Request not found"));
			item.setRequest(request);
		}

		return itemRepository.save(item);
	}

	@Override
	public Item update(Long itemId, Item item, Long userId) {
		Item existing = itemRepository.findById(itemId)
				.orElseThrow(() -> new NotFoundException("Item not found"));

		if (!existing.getOwner().getId().equals(userId)) {
			throw new NotFoundException("Only owner can update item");
		}

		if (item.getName() != null) {
			existing.setName(item.getName());
		}

		if (item.getDescription() != null) {
			existing.setDescription(item.getDescription());
		}

		if (item.getAvailable() != null) {
			existing.setAvailable(item.getAvailable());
		}

		return itemRepository.save(existing);
	}

	@Override
	public Item getById(Long itemId) {
		return itemRepository.findById(itemId)
				.orElseThrow(() -> new NotFoundException("Item not found"));
	}

	@Override
	public Item getById(Long itemId, Long userId) {
		userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found"));

		return getById(itemId);
	}

	@Override
	public ItemDto getDtoById(Long itemId, Long userId) {
		Item item = getById(itemId, userId);
		List<Comment> comments = commentRepository.findByItemIdOrderByCreatedAsc(itemId);

		Booking lastBooking = null;
		Booking nextBooking = null;

		if (item.getOwner().getId().equals(userId)) {
			LocalDateTime now = LocalDateTime.now();

			lastBooking = bookingRepository.findFirstByItemIdAndStatusAndEndBefore(
					itemId,
					BookingStatus.APPROVED,
					now,
					START_DESC
			);

			nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfter(
					itemId,
					BookingStatus.APPROVED,
					now,
					START_ASC
			);
		}

		return ItemMapper.toDto(item, lastBooking, nextBooking, comments);
	}

	@Override
	public List<Item> getAllByUser(Long userId) {
		userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found"));

		return itemRepository.findByOwnerId(userId);
	}

	@Override
	public List<ItemDto> getAllDtoByUser(Long userId) {
		List<Item> items = getAllByUser(userId);
		List<Long> itemIds = items.stream()
				.map(Item::getId)
				.toList();

		Map<Long, List<Comment>> commentsByItemId = commentRepository.findByItemIdInOrderByCreatedAsc(itemIds)
				.stream()
				.collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

		LocalDateTime now = LocalDateTime.now();

		return items.stream()
				.map(item -> {
					Booking lastBooking = bookingRepository.findFirstByItemIdAndStatusAndEndBefore(
							item.getId(),
							BookingStatus.APPROVED,
							now,
							START_DESC
					);

					Booking nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfter(
							item.getId(),
							BookingStatus.APPROVED,
							now,
							START_ASC
					);

					return ItemMapper.toDto(
							item,
							lastBooking,
							nextBooking,
							commentsByItemId.getOrDefault(item.getId(), List.of())
					);
				})
				.toList();
	}

	@Override
	public List<Item> search(String text) {
		if (text == null || text.isBlank()) {
			return List.of();
		}

		return itemRepository.search(text);
	}

	@Override
	public Comment addComment(Long itemId, Long userId, String text) {
		Item item = itemRepository.findById(itemId)
				.orElseThrow(() -> new NotFoundException("Item not found"));

		User author = userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found"));

		boolean hasCompletedBooking = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
				itemId,
				userId,
				BookingStatus.APPROVED,
				LocalDateTime.now()
		);

		if (!hasCompletedBooking) {
			throw new ValidationException("Only user with completed booking can comment item");
		}

		Comment comment = new Comment();
		comment.setText(text);
		comment.setItem(item);
		comment.setAuthor(author);
		comment.setCreated(LocalDateTime.now());

		return commentRepository.save(comment);
	}
}