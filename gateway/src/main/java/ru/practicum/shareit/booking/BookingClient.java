package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Component
public class BookingClient extends BaseClient {
	private static final String API_PREFIX = "/bookings";

	public BookingClient(@Value("${shareit-server.url}") String serverUrl) {
		super(serverUrl);
	}

	public ResponseEntity<Object> create(Long userId, BookingDto bookingDto) {
		return post(API_PREFIX, userId, bookingDto);
	}

	public ResponseEntity<Object> approve(Long userId, Long bookingId, Boolean approved) {
		return makeAndSendRequest(
				HttpMethod.PATCH,
				API_PREFIX + "/" + bookingId + "?approved={approved}",
				userId,
				Map.of("approved", approved),
				null
		);
	}

	public ResponseEntity<Object> getById(Long userId, Long bookingId) {
		return get(API_PREFIX + "/" + bookingId, userId);
	}

	public ResponseEntity<Object> getUserBookings(Long userId, String state) {
		return get(API_PREFIX + "?state={state}", userId, Map.of("state", state));
	}

	public ResponseEntity<Object> getOwnerBookings(Long userId, String state) {
		return get(API_PREFIX + "/owner?state={state}", userId, Map.of("state", state));
	}
}