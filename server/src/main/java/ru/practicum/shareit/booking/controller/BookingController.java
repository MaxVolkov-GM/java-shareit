package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

	private final BookingService bookingService;

	@PostMapping
	public BookingDto create(
			@Valid @RequestBody BookingCreateDto bookingCreateDto,
			@RequestHeader("X-Sharer-User-Id") Long userId
	) {
		return bookingService.create(bookingCreateDto, userId);
	}

	@PatchMapping("/{bookingId}")
	public BookingDto approve(
			@PathVariable Long bookingId,
			@RequestParam Boolean approved,
			@RequestHeader("X-Sharer-User-Id") Long userId
	) {
		return bookingService.approve(bookingId, userId, approved);
	}

	@GetMapping("/{bookingId}")
	public BookingDto getById(
			@PathVariable Long bookingId,
			@RequestHeader("X-Sharer-User-Id") Long userId
	) {
		return bookingService.getById(bookingId, userId);
	}

	@GetMapping
	public List<BookingDto> getAllByUser(
			@RequestHeader("X-Sharer-User-Id") Long userId,
			@RequestParam(defaultValue = "ALL") BookingState state,
			@RequestParam(defaultValue = "0") int from,
			@RequestParam(defaultValue = "10") int size
	) {
		return bookingService.getAllByUser(userId, state, from, size);
	}

	@GetMapping("/owner")
	public List<BookingDto> getAllByOwner(
			@RequestHeader("X-Sharer-User-Id") Long userId,
			@RequestParam(defaultValue = "ALL") BookingState state,
			@RequestParam(defaultValue = "0") int from,
			@RequestParam(defaultValue = "10") int size
	) {
		return bookingService.getAllByOwner(userId, state, from, size);
	}
}