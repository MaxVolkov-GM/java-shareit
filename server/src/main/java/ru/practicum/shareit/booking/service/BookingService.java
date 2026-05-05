package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Booking;

import java.util.List;

public interface BookingService {
	Booking create(Long userId, Booking booking);

	Booking approve(Long ownerId, Long bookingId, Boolean approved);

	Booking getById(Long userId, Long bookingId);

	List<Booking> getUserBookings(Long userId, String state);

	List<Booking> getOwnerBookings(Long ownerId, String state);
}
