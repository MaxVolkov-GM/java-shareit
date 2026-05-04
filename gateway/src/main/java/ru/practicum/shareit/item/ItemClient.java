package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Component
public class ItemClient extends BaseClient {

	private static final String API_PREFIX = "/items";

	public ItemClient(RestTemplate restTemplate,
	                  @Value("${shareit-server.url}") String serverUrl) {
		super(restTemplate, serverUrl);
	}

	public ResponseEntity<Object> create(Long userId, ItemDto itemDto) {
		return post(API_PREFIX, userId, itemDto);
	}

	public ResponseEntity<Object> update(Long userId, Long itemId, ItemDto itemDto) {
		return patch(API_PREFIX + "/" + itemId, userId, itemDto);
	}

	public ResponseEntity<Object> getById(Long userId, Long itemId) {
		return get(API_PREFIX + "/" + itemId, userId);
	}

	public ResponseEntity<Object> getAll(Long userId) {
		return get(API_PREFIX, userId);
	}

	public ResponseEntity<Object> search(String text) {
		return get(API_PREFIX + "/search?text={text}", null, java.util.Map.of("text", text));
	}

	public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentDto commentDto) {
		return post(API_PREFIX + "/" + itemId + "/comment", userId, commentDto);
	}
}