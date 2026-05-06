package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class BookingClientTest {

	private static final String SERVER_URL = "http://localhost:9090";
	private static final String USER_ID_HEADER = "X-Sharer-User-Id";

	private BookingClient bookingClient;
	private MockRestServiceServer mockServer;

	@BeforeEach
	void setUp() {
		bookingClient = new BookingClient(SERVER_URL);
		RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(bookingClient, "rest");
		mockServer = MockRestServiceServer.bindTo(restTemplate).build();
	}

	@Test
	void create_shouldSendPostRequest() {
		LocalDateTime start = LocalDateTime.of(2026, 5, 6, 15, 0);
		LocalDateTime end = LocalDateTime.of(2026, 5, 6, 16, 0);

		BookingDto bookingDto = new BookingDto();
		bookingDto.setStart(start);
		bookingDto.setEnd(end);
		bookingDto.setItemId(1L);

		mockServer.expect(once(), requestTo(SERVER_URL + "/bookings"))
				.andExpect(method(HttpMethod.POST))
				.andExpect(header(USER_ID_HEADER, "1"))
				.andRespond(withSuccess("""
                        {
                            "id": 1,
                            "status": "WAITING"
                        }
                        """, MediaType.APPLICATION_JSON));

		assertEquals(HttpStatus.OK, bookingClient.create(1L, bookingDto).getStatusCode());
		mockServer.verify();
	}

	@Test
	void approve_shouldSendPatchRequest() {
		mockServer.expect(once(), requestTo(SERVER_URL + "/bookings/1?approved=true"))
				.andExpect(method(HttpMethod.PATCH))
				.andExpect(header(USER_ID_HEADER, "1"))
				.andRespond(withSuccess("""
                        {
                            "id": 1,
                            "status": "APPROVED"
                        }
                        """, MediaType.APPLICATION_JSON));

		assertEquals(HttpStatus.OK, bookingClient.approve(1L, 1L, true).getStatusCode());
		mockServer.verify();
	}

	@Test
	void getById_shouldSendGetRequest() {
		mockServer.expect(once(), requestTo(SERVER_URL + "/bookings/1"))
				.andExpect(method(HttpMethod.GET))
				.andExpect(header(USER_ID_HEADER, "1"))
				.andRespond(withSuccess("""
                        {
                            "id": 1,
                            "status": "APPROVED"
                        }
                        """, MediaType.APPLICATION_JSON));

		assertEquals(HttpStatus.OK, bookingClient.getById(1L, 1L).getStatusCode());
		mockServer.verify();
	}

	@Test
	void getUserBookings_shouldSendGetRequest() {
		mockServer.expect(once(), requestTo(SERVER_URL + "/bookings?state=ALL&from=0&size=10"))
				.andExpect(method(HttpMethod.GET))
				.andExpect(header(USER_ID_HEADER, "1"))
				.andRespond(withSuccess("""
                        [
                            {
                                "id": 1,
                                "status": "APPROVED"
                            }
                        ]
                        """, MediaType.APPLICATION_JSON));

		assertEquals(HttpStatus.OK, bookingClient.getUserBookings(1L, "ALL", 0, 10).getStatusCode());
		mockServer.verify();
	}

	@Test
	void getOwnerBookings_shouldSendGetRequest() {
		mockServer.expect(once(), requestTo(SERVER_URL + "/bookings/owner?state=ALL&from=0&size=10"))
				.andExpect(method(HttpMethod.GET))
				.andExpect(header(USER_ID_HEADER, "1"))
				.andRespond(withSuccess("""
                        [
                            {
                                "id": 1,
                                "status": "APPROVED"
                            }
                        ]
                        """, MediaType.APPLICATION_JSON));

		assertEquals(HttpStatus.OK, bookingClient.getOwnerBookings(1L, "ALL", 0, 10).getStatusCode());
		mockServer.verify();
	}
}