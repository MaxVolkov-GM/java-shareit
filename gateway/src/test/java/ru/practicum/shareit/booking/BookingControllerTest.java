package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

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
	}

	@Test
	void approveBooking_thenStatusOk() throws Exception {
		mockMvc.perform(patch("/bookings/1")
						.header("X-Sharer-User-Id", 1L)
						.param("approved", "true"))
				.andExpect(status().isOk());
	}

	@Test
	void getBookingById_thenStatusOk() throws Exception {
		mockMvc.perform(get("/bookings/1")
						.header("X-Sharer-User-Id", 1L))
				.andExpect(status().isOk());
	}

	@Test
	void getUserBookings_thenStatusOk() throws Exception {
		mockMvc.perform(get("/bookings")
						.header("X-Sharer-User-Id", 1L)
						.param("state", "ALL")
						.param("from", "0")
						.param("size", "10"))
				.andExpect(status().isOk());
	}

	@Test
	void getOwnerBookings_thenStatusOk() throws Exception {
		mockMvc.perform(get("/bookings/owner")
						.header("X-Sharer-User-Id", 1L)
						.param("state", "ALL")
						.param("from", "0")
						.param("size", "10"))
				.andExpect(status().isOk());
	}
}