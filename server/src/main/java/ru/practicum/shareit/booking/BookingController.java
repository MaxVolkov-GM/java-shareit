package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
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
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
	private final BookingService bookingService;

	@PostMapping
	public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
	                         @RequestBody BookingDto bookingDto) {
		return BookingMapper.toDto(bookingService.create(userId, BookingMapper.toBooking(bookingDto)));
	}

	@PatchMapping("/{bookingId}")
	public BookingDto approve(@RequestHeader("X-Sharer-User-Id") Long userId,
	                          @PathVariable Long bookingId,
	                          @RequestParam Boolean approved) {
		return BookingMapper.toDto(bookingService.approve(userId, bookingId, approved));
	}

	@GetMapping("/{bookingId}")
	public BookingDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
	                          @PathVariable Long bookingId) {
		return BookingMapper.toDto(bookingService.getById(userId, bookingId));
	}

	@GetMapping
	public List<BookingDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                        @RequestParam(defaultValue = "ALL") String state) {
		return bookingService.getUserBookings(userId, state).stream()
				.map(BookingMapper::toDto)
				.toList();
	}

	@GetMapping("/owner")
	public List<BookingDto> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                         @RequestParam(defaultValue = "ALL") String state) {
		return bookingService.getOwnerBookings(userId, state).stream()
				.map(BookingMapper::toDto)
				.toList();
	}
}
