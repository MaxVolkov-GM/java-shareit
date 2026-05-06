package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceValidationTest {

	@Autowired
	private BookingService bookingService;

	@Autowired
	private ItemService itemService;

	@Autowired
	private UserService userService;

	@Test
	void create_whenOwnerBooksOwnItem_thenThrowsNotFoundException() {
		User owner = userService.create(new User(null, "Owner", "booking-validation-owner-own@mail.com"));
		Item item = itemService.create(
				new Item(null, "Drill", "Description", true, owner, null),
				owner.getId()
		);

		Booking booking = new Booking();
		booking.setItem(item);
		booking.setStart(LocalDateTime.now().plusDays(1));
		booking.setEnd(LocalDateTime.now().plusDays(2));

		assertThrows(NotFoundException.class, () -> bookingService.create(owner.getId(), booking));
	}

	@Test
	void create_whenItemDoesNotExist_thenThrowsNotFoundException() {
		User booker = userService.create(new User(null, "Booker", "booking-validation-no-item@mail.com"));

		Item item = new Item();
		item.setId(99999L);

		Booking booking = new Booking();
		booking.setItem(item);
		booking.setStart(LocalDateTime.now().plusDays(1));
		booking.setEnd(LocalDateTime.now().plusDays(2));

		assertThrows(NotFoundException.class, () -> bookingService.create(booker.getId(), booking));
	}

	@Test
	void create_whenItemIsNotAvailable_thenThrowsValidationException() {
		User owner = userService.create(new User(null, "Owner", "booking-validation-owner-not-available@mail.com"));
		User booker = userService.create(new User(null, "Booker", "booking-validation-booker-not-available@mail.com"));
		Item item = itemService.create(
				new Item(null, "Drill", "Description", false, owner, null),
				owner.getId()
		);

		Booking booking = new Booking();
		booking.setItem(item);
		booking.setStart(LocalDateTime.now().plusDays(1));
		booking.setEnd(LocalDateTime.now().plusDays(2));

		assertThrows(ValidationException.class, () -> bookingService.create(booker.getId(), booking));
	}

	@Test
	void create_whenEndBeforeStart_thenThrowsValidationException() {
		User owner = userService.create(new User(null, "Owner", "booking-validation-owner-dates@mail.com"));
		User booker = userService.create(new User(null, "Booker", "booking-validation-booker-dates@mail.com"));
		Item item = itemService.create(
				new Item(null, "Drill", "Description", true, owner, null),
				owner.getId()
		);

		Booking booking = new Booking();
		booking.setItem(item);
		booking.setStart(LocalDateTime.now().plusDays(2));
		booking.setEnd(LocalDateTime.now().plusDays(1));

		assertThrows(ValidationException.class, () -> bookingService.create(booker.getId(), booking));
	}

	@Test
	void approve_whenUserIsNotOwner_thenThrowsForbiddenException() {
		User owner = userService.create(new User(null, "Owner", "booking-validation-owner-forbidden@mail.com"));
		User booker = userService.create(new User(null, "Booker", "booking-validation-booker-forbidden@mail.com"));
		User other = userService.create(new User(null, "Other", "booking-validation-other-forbidden@mail.com"));
		Item item = itemService.create(
				new Item(null, "Drill", "Description", true, owner, null),
				owner.getId()
		);

		Booking booking = new Booking();
		booking.setItem(item);
		booking.setStart(LocalDateTime.now().plusDays(1));
		booking.setEnd(LocalDateTime.now().plusDays(2));

		Booking created = bookingService.create(booker.getId(), booking);

		assertThrows(ForbiddenException.class, () -> bookingService.approve(other.getId(), created.getId(), true));
	}

	@Test
	void approve_whenBookingAlreadyProcessed_thenThrowsValidationException() {
		User owner = userService.create(new User(null, "Owner", "booking-validation-owner-processed@mail.com"));
		User booker = userService.create(new User(null, "Booker", "booking-validation-booker-processed@mail.com"));
		Item item = itemService.create(
				new Item(null, "Drill", "Description", true, owner, null),
				owner.getId()
		);

		Booking booking = new Booking();
		booking.setItem(item);
		booking.setStart(LocalDateTime.now().plusDays(1));
		booking.setEnd(LocalDateTime.now().plusDays(2));

		Booking created = bookingService.create(booker.getId(), booking);
		bookingService.approve(owner.getId(), created.getId(), true);

		assertThrows(ValidationException.class, () -> bookingService.approve(owner.getId(), created.getId(), false));
	}

	@Test
	void getById_whenUserIsNotOwnerOrBooker_thenThrowsNotFoundException() {
		User owner = userService.create(new User(null, "Owner", "booking-validation-owner-get@mail.com"));
		User booker = userService.create(new User(null, "Booker", "booking-validation-booker-get@mail.com"));
		User other = userService.create(new User(null, "Other", "booking-validation-other-get@mail.com"));
		Item item = itemService.create(
				new Item(null, "Drill", "Description", true, owner, null),
				owner.getId()
		);

		Booking booking = new Booking();
		booking.setItem(item);
		booking.setStart(LocalDateTime.now().plusDays(1));
		booking.setEnd(LocalDateTime.now().plusDays(2));

		Booking created = bookingService.create(booker.getId(), booking);

		assertThrows(NotFoundException.class, () -> bookingService.getById(other.getId(), created.getId()));
	}

	@Test
	void getUserBookings_whenStateIsUnknown_thenThrowsValidationException() {
		User user = userService.create(new User(null, "User", "booking-validation-user-state@mail.com"));

		assertThrows(ValidationException.class, () -> bookingService.getUserBookings(user.getId(), "UNKNOWN", 0, 10));
	}

	@Test
	void getUserBookings_whenFromIsNegative_thenThrowsValidationException() {
		User user = userService.create(new User(null, "User", "booking-validation-user-from@mail.com"));

		assertThrows(ValidationException.class, () -> bookingService.getUserBookings(user.getId(), "ALL", -1, 10));
	}

	@Test
	void getUserBookings_whenSizeIsZero_thenThrowsValidationException() {
		User user = userService.create(new User(null, "User", "booking-validation-user-size@mail.com"));

		assertThrows(ValidationException.class, () -> bookingService.getUserBookings(user.getId(), "ALL", 0, 0));
	}

	@Test
	void getOwnerBookings_whenStateIsUnknown_thenThrowsValidationException() {
		User user = userService.create(new User(null, "User", "booking-validation-owner-state@mail.com"));

		assertThrows(ValidationException.class, () -> bookingService.getOwnerBookings(user.getId(), "UNKNOWN", 0, 10));
	}
}