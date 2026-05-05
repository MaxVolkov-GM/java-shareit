package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceImplTest {

	@Autowired
	private BookingService bookingService;

	@Autowired
	private ItemService itemService;

	@Autowired
	private UserService userService;

	@Test
	void createBooking() {
		User owner = userService.create(new User(null, "Owner", "booking-owner-create@mail.com"));
		User booker = userService.create(new User(null, "Booker", "booking-booker-create@mail.com"));

		Item item = itemService.create(
				new Item(null, "Дрель", "Аккумуляторная дрель", true, owner, null),
				owner.getId()
		);

		Booking booking = new Booking();
		booking.setItem(item);
		booking.setStart(LocalDateTime.now().plusDays(1));
		booking.setEnd(LocalDateTime.now().plusDays(2));

		Booking created = bookingService.create(booker.getId(), booking);

		assertThat(created.getId()).isNotNull();
		assertThat(created.getStatus()).isEqualTo(BookingStatus.WAITING);
		assertThat(created.getBooker().getId()).isEqualTo(booker.getId());
		assertThat(created.getItem().getId()).isEqualTo(item.getId());
	}

	@Test
	void approveBooking() {
		User owner = userService.create(new User(null, "Owner", "booking-owner-approve@mail.com"));
		User booker = userService.create(new User(null, "Booker", "booking-booker-approve@mail.com"));

		Item item = itemService.create(
				new Item(null, "Шуруповерт", "Описание", true, owner, null),
				owner.getId()
		);

		Booking booking = new Booking();
		booking.setItem(item);
		booking.setStart(LocalDateTime.now().plusDays(1));
		booking.setEnd(LocalDateTime.now().plusDays(2));

		Booking created = bookingService.create(booker.getId(), booking);
		Booking approved = bookingService.approve(owner.getId(), created.getId(), true);

		assertThat(approved.getStatus()).isEqualTo(BookingStatus.APPROVED);
	}

	@Test
	void getUserBookings() {
		User owner = userService.create(new User(null, "Owner", "booking-owner-list@mail.com"));
		User booker = userService.create(new User(null, "Booker", "booking-booker-list@mail.com"));

		Item item = itemService.create(
				new Item(null, "Перфоратор", "Описание", true, owner, null),
				owner.getId()
		);

		Booking booking = new Booking();
		booking.setItem(item);
		booking.setStart(LocalDateTime.now().plusDays(1));
		booking.setEnd(LocalDateTime.now().plusDays(2));

		Booking created = bookingService.create(booker.getId(), booking);

		List<Booking> bookings = bookingService.getUserBookings(booker.getId(), "ALL");

		assertThat(bookings).extracting(Booking::getId).contains(created.getId());
	}
}