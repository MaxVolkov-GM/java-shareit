package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Component
public class ItemClient extends BaseClient {
	private static final String API_PREFIX = "/items";

	public ItemClient(@Value("${shareit-server.url}") String serverUrl) {
		super(serverUrl);
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

	public ResponseEntity<Object> getUserItems(Long userId, Integer from, Integer size) {
		return get(API_PREFIX + "?from={from}&size={size}", userId, Map.of(
				"from", from,
				"size", size
		));
	}

	public ResponseEntity<Object> search(Long userId, String text, Integer from, Integer size) {
		return get(API_PREFIX + "/search?text={text}&from={from}&size={size}", userId, Map.of(
				"text", text,
				"from", from,
				"size", size
		));
	}

	public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentDto commentDto) {
		return post(API_PREFIX + "/" + itemId + "/comment", userId, commentDto);
	}
}