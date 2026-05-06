package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.validation.Create;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

	private static final long PAST_BOOKING_TOLERANCE_SECONDS = 5;

	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                     @Validated(Create.class) @RequestBody BookingDto bookingDto) {
		LocalDateTime now = LocalDateTime.now();

		if (bookingDto.getStart().isBefore(now.minusSeconds(PAST_BOOKING_TOLERANCE_SECONDS))) {
			throw new IllegalArgumentException("Booking start must be in the present or in the future");
		}

		if (!bookingDto.getEnd().isAfter(bookingDto.getStart())) {
			throw new IllegalArgumentException("Booking end must be after start");
		}

		return bookingClient.create(userId, bookingDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approve(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                      @PathVariable Long bookingId,
	                                      @RequestParam Boolean approved) {
		return bookingClient.approve(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                      @PathVariable Long bookingId) {
		return bookingClient.getById(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                              @RequestParam(defaultValue = "ALL") String state,
	                                              @RequestParam(defaultValue = "0") Integer from,
	                                              @RequestParam(defaultValue = "10") Integer size) {
		validatePagination(from, size);
		return bookingClient.getUserBookings(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                               @RequestParam(defaultValue = "ALL") String state,
	                                               @RequestParam(defaultValue = "0") Integer from,
	                                               @RequestParam(defaultValue = "10") Integer size) {
		validatePagination(from, size);
		return bookingClient.getOwnerBookings(userId, state, from, size);
	}

	private void validatePagination(Integer from, Integer size) {
		if (from < 0 || size <= 0) {
			throw new IllegalArgumentException("Invalid pagination parameters");
		}
	}
}