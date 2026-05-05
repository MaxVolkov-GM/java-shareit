package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Component
public class ItemRequestClient extends BaseClient {

	public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl) {
		super(serverUrl);
	}

	public ResponseEntity<Object> create(Long userId, ItemRequestDto requestDto) {
		return post("/requests", userId, requestDto);
	}

	public ResponseEntity<Object> getUserRequests(Long userId) {
		return get("/requests", userId);
	}

	public ResponseEntity<Object> getAllRequests(Long userId) {
		return get("/requests/all", userId);
	}

	public ResponseEntity<Object> getById(Long userId, Long requestId) {
		return get("/requests/" + requestId, userId);
	}
}
