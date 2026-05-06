package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class MapperTest {

	@Test
	void userMapper_shouldMapUserToDto() {
		User user = new User(1L, "User", "user@mail.com");

		UserDto dto = UserMapper.toDto(user);

		assertEquals(1L, dto.getId());
		assertEquals("User", dto.getName());
		assertEquals("user@mail.com", dto.getEmail());
	}

	@Test
	void userMapper_shouldMapDtoToUser() {
		UserDto dto = new UserDto(1L, "User", "user@mail.com");

		User user = UserMapper.toUser(dto);

		assertEquals(1L, user.getId());
		assertEquals("User", user.getName());
		assertEquals("user@mail.com", user.getEmail());
	}

	@Test
	void itemMapper_shouldMapItemToDtoWithoutBookingsAndComments() {
		User owner = new User(1L, "Owner", "owner@mail.com");
		ItemRequest request = new ItemRequest();
		request.setId(10L);
		Item item = new Item(1L, "Drill", "Powerful drill", true, owner, request);

		ItemDto dto = ItemMapper.toDto(item);

		assertEquals(1L, dto.getId());
		assertEquals("Drill", dto.getName());
		assertEquals("Powerful drill", dto.getDescription());
		assertEquals(true, dto.getAvailable());
		assertEquals(10L, dto.getRequestId());
		assertNull(dto.getLastBooking());
		assertNull(dto.getNextBooking());
		assertNotNull(dto.getComments());
	}

	@Test
	void itemMapper_shouldMapDtoToItemWithRequest() {
		ItemDto dto = new ItemDto(1L, "Drill", "Powerful drill", true, 10L);

		Item item = ItemMapper.toItem(dto);

		assertEquals(1L, item.getId());
		assertEquals("Drill", item.getName());
		assertEquals("Powerful drill", item.getDescription());
		assertEquals(true, item.getAvailable());
		assertNotNull(item.getRequest());
		assertEquals(10L, item.getRequest().getId());
	}

	@Test
	void itemMapper_shouldMapCommentToDto() {
		User author = new User(1L, "Author", "author@mail.com");
		Item item = new Item(1L, "Drill", "Powerful drill", true, author, null);
		LocalDateTime created = LocalDateTime.of(2026, 5, 6, 17, 0);
		Comment comment = new Comment(1L, "Good item", item, author, created);

		CommentDto dto = ItemMapper.toCommentDto(comment);

		assertEquals(1L, dto.getId());
		assertEquals("Good item", dto.getText());
		assertEquals("Author", dto.getAuthorName());
		assertEquals(created, dto.getCreated());
	}

	@Test
	void bookingMapper_shouldMapBookingToDto() {
		User owner = new User(1L, "Owner", "owner@mail.com");
		User booker = new User(2L, "Booker", "booker@mail.com");
		Item item = new Item(1L, "Drill", "Powerful drill", true, owner, null);
		LocalDateTime start = LocalDateTime.of(2026, 5, 6, 17, 0);
		LocalDateTime end = LocalDateTime.of(2026, 5, 6, 18, 0);
		Booking booking = new Booking(1L, start, end, item, booker, BookingStatus.WAITING);

		BookingDto dto = BookingMapper.toDto(booking);

		assertEquals(1L, dto.getId());
		assertEquals(start, dto.getStart());
		assertEquals(end, dto.getEnd());
		assertEquals(1L, dto.getItemId());
		assertEquals(2L, dto.getBookerId());
		assertEquals(BookingStatus.WAITING, dto.getStatus());
		assertEquals("Drill", dto.getItem().getName());
		assertEquals("Booker", dto.getBooker().getName());
	}

	@Test
	void bookingMapper_shouldMapDtoToBooking() {
		LocalDateTime start = LocalDateTime.of(2026, 5, 6, 17, 0);
		LocalDateTime end = LocalDateTime.of(2026, 5, 6, 18, 0);
		BookingDto dto = new BookingDto();
		dto.setId(1L);
		dto.setStart(start);
		dto.setEnd(end);
		dto.setItemId(10L);
		dto.setBookerId(20L);
		dto.setStatus(BookingStatus.WAITING);

		Booking booking = BookingMapper.toBooking(dto);

		assertEquals(1L, booking.getId());
		assertEquals(start, booking.getStart());
		assertEquals(end, booking.getEnd());
		assertEquals(10L, booking.getItem().getId());
		assertEquals(20L, booking.getBooker().getId());
		assertEquals(BookingStatus.WAITING, booking.getStatus());
	}

	@Test
	void itemRequestMapper_shouldMapRequestToDtoWithItems() {
		User requestor = new User(1L, "Requestor", "requestor@mail.com");
		User owner = new User(2L, "Owner", "owner@mail.com");
		LocalDateTime created = LocalDateTime.of(2026, 5, 6, 17, 0);

		ItemRequest request = new ItemRequest();
		request.setId(1L);
		request.setDescription("Need drill");
		request.setRequestor(requestor);
		request.setCreated(created);

		Item item = new Item(1L, "Drill", "Powerful drill", true, owner, request);

		ru.practicum.shareit.request.dto.ItemRequestDto dto =
				ItemRequestMapper.toDto(request, List.of(item));

		assertEquals(1L, dto.getId());
		assertEquals("Need drill", dto.getDescription());
		assertEquals(1L, dto.getRequestorId());
		assertEquals(created, dto.getCreated());
		assertEquals(1, dto.getItems().size());
		assertEquals(1L, dto.getItems().getFirst().getId());
		assertEquals("Drill", dto.getItems().getFirst().getName());
		assertEquals(2L, dto.getItems().getFirst().getOwnerId());
	}

	@Test
	void itemRequestMapper_shouldMapDtoToEntity() {
		User requestor = new User(1L, "Requestor", "requestor@mail.com");
		ru.practicum.shareit.request.dto.ItemRequestDto dto =
				new ru.practicum.shareit.request.dto.ItemRequestDto();
		dto.setId(1L);
		dto.setDescription("Need drill");

		ItemRequest request = ItemRequestMapper.toEntity(dto, requestor);

		assertEquals(1L, request.getId());
		assertEquals("Need drill", request.getDescription());
		assertEquals(requestor, request.getRequestor());
		assertNotNull(request.getCreated());
	}
}