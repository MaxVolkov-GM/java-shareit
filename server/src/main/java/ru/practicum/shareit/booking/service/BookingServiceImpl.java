package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
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
public class BookingServiceImpl {
	private final BookingRepository bookingRepository;
	private final ItemRepository itemRepository;
	private final UserRepository userRepository;

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

	public List<Booking> getUserBookings(Long userId, String state) {
		userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found"));

		LocalDateTime now = LocalDateTime.now();

		return switch (normalizeState(state)) {
			case "CURRENT" -> bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
			case "PAST" -> bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
			case "FUTURE" -> bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now);
			case "WAITING" -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
			case "REJECTED" -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
			case "ALL" -> bookingRepository.findByBookerIdOrderByStartDesc(userId);
			default -> throw new ValidationException("Unknown state: " + state);
		};
	}

	public List<Booking> getOwnerBookings(Long ownerId, String state) {
		userRepository.findById(ownerId)
				.orElseThrow(() -> new NotFoundException("User not found"));

		LocalDateTime now = LocalDateTime.now();

		return switch (normalizeState(state)) {
			case "CURRENT" -> bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, now, now);
			case "PAST" -> bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now);
			case "FUTURE" -> bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
			case "WAITING" -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
			case "REJECTED" -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
			case "ALL" -> bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
			default -> throw new ValidationException("Unknown state: " + state);
		};
	}

	private String normalizeState(String state) {
		return state == null || state.isBlank() ? "ALL" : state.toUpperCase();
	}
}