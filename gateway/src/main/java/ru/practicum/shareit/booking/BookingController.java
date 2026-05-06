package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
	                                              @RequestParam(defaultValue = "ALL") String state) {
		return bookingClient.getUserBookings(userId, state);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                               @RequestParam(defaultValue = "ALL") String state) {
		return bookingClient.getOwnerBookings(userId, state);
	}
}