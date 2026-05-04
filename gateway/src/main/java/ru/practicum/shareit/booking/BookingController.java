package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                     @Valid @RequestBody BookingCreateDto bookingCreateDto) {
		return bookingClient.create(userId, bookingCreateDto);
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
	public ResponseEntity<Object> getByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                          @RequestParam(defaultValue = "ALL") String state,
	                                          @RequestParam(defaultValue = "0") Integer from,
	                                          @RequestParam(defaultValue = "10") Integer size) {
		return bookingClient.getByBooker(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                         @RequestParam(defaultValue = "ALL") String state,
	                                         @RequestParam(defaultValue = "0") Integer from,
	                                         @RequestParam(defaultValue = "10") Integer size) {
		return bookingClient.getByOwner(userId, state, from, size);
	}
}