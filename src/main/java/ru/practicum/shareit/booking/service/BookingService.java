package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
	BookingDto create(BookingCreateDto bookingCreateDto, Long userId);

	BookingDto approve(Long bookingId, Long userId, Boolean approved);

	BookingDto getById(Long bookingId, Long userId);

	List<BookingDto> getAllByUser(Long userId, BookingState state, int from, int size);

	List<BookingDto> getAllByOwner(Long userId, BookingState state, int from, int size);
}