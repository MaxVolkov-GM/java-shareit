package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

public class BookingMapper {
	private BookingMapper() {
	}

	public static BookingDto toDto(Booking booking) {
		return new BookingDto(
				booking.getId(),
				booking.getStart(),
				booking.getEnd(),
				ItemMapper.toDto(booking.getItem()),
				UserMapper.toDto(booking.getBooker()),
				booking.getStatus()
		);
	}
}