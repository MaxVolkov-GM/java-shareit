package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRequest;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

	private final ItemRepository itemRepository;
	private final UserRepository userRepository;
	private final ItemRequestRepository itemRequestRepository;
	private final BookingRepository bookingRepository;
	private final CommentRepository commentRepository;

	@Override
	public Item create(Item item, Long userId) {
		User owner = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		item.setOwner(owner);

		if (item.getRequest() != null && item.getRequest().getId() != null) {
			ItemRequest request = itemRequestRepository.findById(item.getRequest().getId())
					.orElseThrow(() -> new RuntimeException("Request not found"));
			item.setRequest(request);
		}

		return itemRepository.save(item);
	}

	@Override
	public Item update(Long itemId, Item item, Long userId) {
		Item existing = itemRepository.findById(itemId)
				.orElseThrow(() -> new RuntimeException("Item not found"));

		if (!existing.getOwner().getId().equals(userId)) {
			throw new RuntimeException("Only owner can update item");
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
				.orElseThrow(() -> new RuntimeException("Item not found"));
	}

	@Override
	public Item getById(Long itemId, Long userId) {
		userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		return getById(itemId);
	}

	@Override
	public List<Item> getAllByUser(Long userId) {
		userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		return itemRepository.findByOwnerId(userId);
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
				.orElseThrow(() -> new RuntimeException("Item not found"));

		User author = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		boolean hasCompletedBooking = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
				itemId,
				userId,
				BookingStatus.APPROVED,
				LocalDateTime.now()
		);

		if (!hasCompletedBooking) {
			throw new RuntimeException("Only user with completed booking can comment item");
		}

		Comment comment = new Comment();
		comment.setText(text);
		comment.setItem(item);
		comment.setAuthor(author);
		comment.setCreated(LocalDateTime.now());

		return commentRepository.save(comment);
	}
}