package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                     @Validated(BookingDto.Create.class) @RequestBody BookingDto bookingDto) {
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