package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
	private final BookingRepository bookingRepository;
	private final ItemRepository itemRepository;
	private final UserRepository userRepository;

	@Override
	public Booking create(Long userId, Booking booking) {
		User booker = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));
		Item item = itemRepository.findById(booking.getItem().getId())
				.orElseThrow(() -> new RuntimeException("Item not found"));

		if (item.getOwner().getId().equals(userId)) {
			throw new RuntimeException("Owner cannot book own item");
		}
		if (!Boolean.TRUE.equals(item.getAvailable())) {
			throw new IllegalArgumentException("Item is not available");
		}
		if (booking.getStart() == null || booking.getEnd() == null || !booking.getEnd().isAfter(booking.getStart())) {
			throw new IllegalArgumentException("Invalid booking dates");
		}

		booking.setBooker(booker);
		booking.setItem(item);
		booking.setStatus(BookingStatus.WAITING);
		return bookingRepository.save(booking);
	}

	@Override
	public Booking approve(Long ownerId, Long bookingId, Boolean approved) {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new RuntimeException("Booking not found"));

		if (!booking.getItem().getOwner().getId().equals(ownerId)) {
			throw new RuntimeException("Only item owner can approve booking");
		}
		if (booking.getStatus() != BookingStatus.WAITING) {
			throw new IllegalArgumentException("Booking has already been processed");
		}

		booking.setStatus(Boolean.TRUE.equals(approved) ? BookingStatus.APPROVED : BookingStatus.REJECTED);
		return bookingRepository.save(booking);
	}

	@Override
	public Booking getById(Long userId, Long bookingId) {
		userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new RuntimeException("Booking not found"));

		Long ownerId = booking.getItem().getOwner().getId();
		Long bookerId = booking.getBooker().getId();
		if (!ownerId.equals(userId) && !bookerId.equals(userId)) {
			throw new RuntimeException("Booking is not available for this user");
		}
		return booking;
	}

	@Override
	public List<Booking> getUserBookings(Long userId, String state) {
		userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
		LocalDateTime now = LocalDateTime.now();
		return switch (normalizeState(state)) {
			case "CURRENT" -> bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
			case "PAST" -> bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
			case "FUTURE" -> bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now);
			case "WAITING" -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
			case "REJECTED" -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
			case "ALL" -> bookingRepository.findByBookerIdOrderByStartDesc(userId);
			default -> throw new IllegalArgumentException("Unknown state: " + state);
		};
	}

	@Override
	public List<Booking> getOwnerBookings(Long ownerId, String state) {
		userRepository.findById(ownerId).orElseThrow(() -> new RuntimeException("User not found"));
		LocalDateTime now = LocalDateTime.now();
		return switch (normalizeState(state)) {
			case "CURRENT" -> bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, now, now);
			case "PAST" -> bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now);
			case "FUTURE" -> bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
			case "WAITING" -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
			case "REJECTED" -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
			case "ALL" -> bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
			default -> throw new IllegalArgumentException("Unknown state: " + state);
		};
	}

	private String normalizeState(String state) {
		return state == null || state.isBlank() ? "ALL" : state.toUpperCase();
	}
}
