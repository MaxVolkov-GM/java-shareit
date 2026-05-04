package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Component
public class BookingClient extends BaseClient {

	private static final String API_PREFIX = "/bookings";

	public BookingClient(RestTemplate restTemplate,
	                     @Value("${shareit-server.url}") String serverUrl) {
		super(restTemplate, serverUrl);
	}

	public ResponseEntity<Object> create(Long userId, BookingCreateDto bookingCreateDto) {
		return post(API_PREFIX, userId, bookingCreateDto);
	}

	public ResponseEntity<Object> approve(Long userId, Long bookingId, Boolean approved) {
		return patch(
				API_PREFIX + "/" + bookingId + "?approved={approved}",
				userId,
				null,
				Map.of("approved", approved)
		);
	}

	public ResponseEntity<Object> getById(Long userId, Long bookingId) {
		return get(API_PREFIX + "/" + bookingId, userId);
	}

	public ResponseEntity<Object> getByBooker(Long userId, String state, Integer from, Integer size) {
		return get(
				API_PREFIX + "?state={state}&from={from}&size={size}",
				userId,
				Map.of("state", state, "from", from, "size", size)
		);
	}

	public ResponseEntity<Object> getByOwner(Long userId, String state, Integer from, Integer size) {
		return get(
				API_PREFIX + "/owner?state={state}&from={from}&size={size}",
				userId,
				Map.of("state", state, "from", from, "size", size)
		);
	}
}