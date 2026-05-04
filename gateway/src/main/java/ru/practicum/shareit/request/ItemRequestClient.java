package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Map;

@Component
public class ItemRequestClient extends BaseClient {

	private static final String API_PREFIX = "/requests";

	public ItemRequestClient(RestTemplate restTemplate,
	                         @Value("${shareit-server.url}") String serverUrl) {
		super(restTemplate, serverUrl);
	}

	public ResponseEntity<Object> create(Long userId, ItemRequestDto dto) {
		return post(API_PREFIX, userId, dto);
	}

	public ResponseEntity<Object> getOwn(Long userId) {
		return get(API_PREFIX, userId);
	}

	public ResponseEntity<Object> getAll(Long userId, Integer from, Integer size) {
		return get(
				API_PREFIX + "/all?from={from}&size={size}",
				userId,
				Map.of("from", from, "size", size)
		);
	}

	public ResponseEntity<Object> getById(Long userId, Long requestId) {
		return get(API_PREFIX + "/" + requestId, userId);
	}
}