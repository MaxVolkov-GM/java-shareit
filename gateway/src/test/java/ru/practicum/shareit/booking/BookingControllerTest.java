package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookingClient bookingClient;

	@Test
	void createBooking_whenValidBooking_thenStatusOk() throws Exception {
		String start = LocalDateTime.now().plusDays(1).toString();
		String end = LocalDateTime.now().plusDays(2).toString();

		String json = """
                {
                  "itemId": 1,
                  "start": "%s",
                  "end": "%s"
                }
                """.formatted(start, end);

		mockMvc.perform(post("/bookings")
						.header("X-Sharer-User-Id", 1L)
						.contentType(APPLICATION_JSON)
						.content(json))
				.andExpect(status().isOk());

		verify(bookingClient).create(1L, new BookingDto(null, LocalDateTime.parse(start),
				LocalDateTime.parse(end), 1L));
	}

	@Test
	void createBooking_whenStartInPast_thenStatusBadRequest() throws Exception {
		String start = LocalDateTime.now().minusDays(1).toString();
		String end = LocalDateTime.now().plusDays(1).toString();

		String json = """
                {
                  "itemId": 1,
                  "start": "%s",
                  "end": "%s"
                }
                """.formatted(start, end);

		mockMvc.perform(post("/bookings")
						.header("X-Sharer-User-Id", 1L)
						.contentType(APPLICATION_JSON)
						.content(json))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(bookingClient);
	}

	@Test
	void createBooking_whenEndBeforeStart_thenStatusBadRequest() throws Exception {
		String start = LocalDateTime.now().plusDays(2).toString();
		String end = LocalDateTime.now().plusDays(1).toString();

		String json = """
                {
                  "itemId": 1,
                  "start": "%s",
                  "end": "%s"
                }
                """.formatted(start, end);

		mockMvc.perform(post("/bookings")
						.header("X-Sharer-User-Id", 1L)
						.contentType(APPLICATION_JSON)
						.content(json))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(bookingClient);
	}

	@Test
	void createBooking_whenItemIdIsNull_thenStatusBadRequest() throws Exception {
		String start = LocalDateTime.now().plusDays(1).toString();
		String end = LocalDateTime.now().plusDays(2).toString();

		String json = """
                {
                  "start": "%s",
                  "end": "%s"
                }
                """.formatted(start, end);

		mockMvc.perform(post("/bookings")
						.header("X-Sharer-User-Id", 1L)
						.contentType(APPLICATION_JSON)
						.content(json))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(bookingClient);
	}

	@Test
	void approveBooking_thenStatusOk() throws Exception {
		mockMvc.perform(patch("/bookings/1")
						.header("X-Sharer-User-Id", 1L)
						.param("approved", "true"))
				.andExpect(status().isOk());

		verify(bookingClient).approve(1L, 1L, true);
	}

	@Test
	void getBookingById_thenStatusOk() throws Exception {
		mockMvc.perform(get("/bookings/1")
						.header("X-Sharer-User-Id", 1L))
				.andExpect(status().isOk());

		verify(bookingClient).getById(1L, 1L);
	}

	@Test
	void getUserBookings_thenStatusOk() throws Exception {
		mockMvc.perform(get("/bookings")
						.header("X-Sharer-User-Id", 1L)
						.param("state", "ALL")
						.param("from", "0")
						.param("size", "10"))
				.andExpect(status().isOk());

		verify(bookingClient).getUserBookings(1L, "ALL", 0, 10);
	}

	@Test
	void getOwnerBookings_thenStatusOk() throws Exception {
		mockMvc.perform(get("/bookings/owner")
						.header("X-Sharer-User-Id", 1L)
						.param("state", "ALL")
						.param("from", "0")
						.param("size", "10"))
				.andExpect(status().isOk());

		verify(bookingClient).getOwnerBookings(1L, "ALL", 0, 10);
	}

	@Test
	void getUserBookings_whenFromIsNegative_thenStatusBadRequest() throws Exception {
		mockMvc.perform(get("/bookings")
						.header("X-Sharer-User-Id", 1L)
						.param("state", "ALL")
						.param("from", "-1")
						.param("size", "10"))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(bookingClient);
	}

	@Test
	void getUserBookings_whenSizeIsZero_thenStatusBadRequest() throws Exception {
		mockMvc.perform(get("/bookings")
						.header("X-Sharer-User-Id", 1L)
						.param("state", "ALL")
						.param("from", "0")
						.param("size", "0"))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(bookingClient);
	}

	@Test
	void getOwnerBookings_whenFromIsNegative_thenStatusBadRequest() throws Exception {
		mockMvc.perform(get("/bookings/owner")
						.header("X-Sharer-User-Id", 1L)
						.param("state", "ALL")
						.param("from", "-1")
						.param("size", "10"))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(bookingClient);
	}

	@Test
	void getOwnerBookings_whenSizeIsZero_thenStatusBadRequest() throws Exception {
		mockMvc.perform(get("/bookings/owner")
						.header("X-Sharer-User-Id", 1L)
						.param("state", "ALL")
						.param("from", "0")
						.param("size", "0"))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(bookingClient);
	}
}