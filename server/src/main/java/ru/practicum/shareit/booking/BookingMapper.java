package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

public class BookingMapper {

	public static BookingDto toDto(Booking booking) {
		Item item = booking.getItem();
		User booker = booking.getBooker();

		return new BookingDto(
				booking.getId(),
				booking.getStart(),
				booking.getEnd(),
				item != null ? item.getId() : null,
				booker != null ? booker.getId() : null,
				item != null ? new ItemShortDto(
						item.getId(),
						item.getName(),
						item.getOwner() != null ? item.getOwner().getId() : null
				) : null,
				booker != null ? new UserDto(
						booker.getId(),
						booker.getName(),
						booker.getEmail()
				) : null,
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