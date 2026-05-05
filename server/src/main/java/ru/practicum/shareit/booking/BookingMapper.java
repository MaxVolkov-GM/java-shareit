package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

public class BookingMapper {

	public static BookingDto toDto(Booking booking) {
		return new BookingDto(
				booking.getId(),
				booking.getStart(),
				booking.getEnd(),
				booking.getItem() != null ? booking.getItem().getId() : null,
				booking.getBooker() != null ? booking.getBooker().getId() : null,
				booking.getStatus()
		);
	}

	public static Booking toBooking(BookingDto bookingDto) {
		Booking booking = new Booking();

		booking.setId(bookingDto.getId());
		booking.setStart(bookingDto.getStart());
		booking.setEnd(bookingDto.getEnd());

		if (bookingDto.getItemId() != null) {
			Item item = new Item();
			item.setId(bookingDto.getItemId());
			booking.setItem(item);
		}

		if (bookingDto.getBookerId() != null) {
			User booker = new User();
			booker.setId(bookingDto.getBookerId());
			booking.setBooker(booker);
		}

		booking.setStatus(bookingDto.getStatus());

		return booking;
	}
}