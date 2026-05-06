package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

	private static final Sort START_DESC = Sort.by(Sort.Direction.DESC, "start");

	private final BookingRepository bookingRepository;
	private final ItemRepository itemRepository;
	private final UserRepository userRepository;

	@Override
	public Booking create(Long userId, Booking booking) {
		User booker = userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found"));

		if (booking.getItem() == null || booking.getItem().getId() == null) {
			throw new NotFoundException("Item not found");
		}

		Item item = itemRepository.findById(booking.getItem().getId())
				.orElseThrow(() -> new NotFoundException("Item not found"));

		if (item.getOwner().getId().equals(userId)) {
			throw new NotFoundException("Owner cannot book own item");
		}

		if (!Boolean.TRUE.equals(item.getAvailable())) {
			throw new ValidationException("Item is not available");
		}

		if (booking.getStart() == null || booking.getEnd() == null || !booking.getEnd().isAfter(booking.getStart())) {
			throw new ValidationException("Invalid booking dates");
		}

		booking.setBooker(booker);
		booking.setItem(item);
		booking.setStatus(BookingStatus.WAITING);

		return bookingRepository.save(booking);
	}

	@Override
	public Booking approve(Long ownerId, Long bookingId, Boolean approved) {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new NotFoundException("Booking not found"));

		if (!booking.getItem().getOwner().getId().equals(ownerId)) {
			throw new ForbiddenException("Only item owner can approve booking");
		}

		if (booking.getStatus() != BookingStatus.WAITING) {
			throw new ValidationException("Booking has already been processed");
		}

		booking.setStatus(Boolean.TRUE.equals(approved) ? BookingStatus.APPROVED : BookingStatus.REJECTED);

		return bookingRepository.save(booking);
	}

	@Override
	public Booking getById(Long userId, Long bookingId) {
		userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found"));

		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new NotFoundException("Booking not found"));

		Long ownerId = booking.getItem().getOwner().getId();
		Long bookerId = booking.getBooker().getId();

		if (!ownerId.equals(userId) && !bookerId.equals(userId)) {
			throw new NotFoundException("Booking is not available for this user");
		}

		return booking;
	}

	@Override
	public List<Booking> getUserBookings(Long userId, String state, Integer from, Integer size) {
		userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found"));

		LocalDateTime now = LocalDateTime.now();
		PageRequest pageRequest = createPageRequest(from, size);

		return switch (normalizeState(state)) {
			case "CURRENT" -> bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(userId, now, now, pageRequest);
			case "PAST" -> bookingRepository.findByBookerIdAndEndBefore(userId, now, pageRequest);
			case "FUTURE" -> bookingRepository.findByBookerIdAndStartAfter(userId, now, pageRequest);
			case "WAITING" -> bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, pageRequest);
			case "REJECTED" -> bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, pageRequest);
			case "ALL" -> bookingRepository.findByBookerId(userId, pageRequest);
			default -> throw new ValidationException("Unknown state: " + state);
		};
	}

	@Override
	public List<Booking> getOwnerBookings(Long ownerId, String state, Integer from, Integer size) {
		userRepository.findById(ownerId)
				.orElseThrow(() -> new NotFoundException("User not found"));

		LocalDateTime now = LocalDateTime.now();
		PageRequest pageRequest = createPageRequest(from, size);

		return switch (normalizeState(state)) {
			case "CURRENT" -> bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfter(ownerId, now, now,
					pageRequest);
			case "PAST" -> bookingRepository.findByItemOwnerIdAndEndBefore(ownerId, now, pageRequest);
			case "FUTURE" -> bookingRepository.findByItemOwnerIdAndStartAfter(ownerId, now, pageRequest);
			case "WAITING" -> bookingRepository.findByItemOwnerIdAndStatus(ownerId, BookingStatus.WAITING, pageRequest);
			case "REJECTED" -> bookingRepository.findByItemOwnerIdAndStatus(ownerId, BookingStatus.REJECTED,
					pageRequest);
			case "ALL" -> bookingRepository.findByItemOwnerId(ownerId, pageRequest);
			default -> throw new ValidationException("Unknown state: " + state);
		};
	}

	private PageRequest createPageRequest(Integer from, Integer size) {
		int pageFrom = from == null ? 0 : from;
		int pageSize = size == null ? 10 : size;

		if (pageFrom < 0 || pageSize <= 0) {
			throw new ValidationException("Invalid pagination parameters");
		}

		return PageRequest.of(pageFrom / pageSize, pageSize, START_DESC);
	}

	private String normalizeState(String state) {
		return state == null || state.isBlank() ? "ALL" : state.toUpperCase();
	}
}