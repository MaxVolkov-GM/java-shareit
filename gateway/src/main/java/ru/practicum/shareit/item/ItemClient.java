package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.item.dto.ItemDto;

@Component
public class ItemClient {

	private final RestTemplate restTemplate;
	private final String serverUrl;

	public ItemClient(RestTemplate restTemplate,
	                  @Value("${shareit-server.url}") String serverUrl) {
		this.restTemplate = restTemplate;
		this.serverUrl = serverUrl;
	}

	public ResponseEntity<Object> create(Long userId, ItemDto itemDto) {
		return restTemplate.exchange(
				serverUrl + "/items",
				HttpMethod.POST,
				new HttpEntity<>(itemDto, headers(userId)),
				Object.class
		);
	}

	public ResponseEntity<Object> update(Long userId, Long itemId, ItemDto itemDto) {
		return restTemplate.exchange(
				serverUrl + "/items/" + itemId,
				HttpMethod.PATCH,
				new HttpEntity<>(itemDto, headers(userId)),
				Object.class
		);
	}

	public ResponseEntity<Object> getById(Long userId, Long itemId) {
		return restTemplate.exchange(
				serverUrl + "/items/" + itemId,
				HttpMethod.GET,
				new HttpEntity<>(headers(userId)),
				Object.class
		);
	}

	public ResponseEntity<Object> getUserItems(Long userId, Integer from, Integer size) {
		String url = UriComponentsBuilder.fromHttpUrl(serverUrl + "/items")
				.queryParam("from", from)
				.queryParam("size", size)
				.toUriString();

		return restTemplate.exchange(
				url,
				HttpMethod.GET,
				new HttpEntity<>(headers(userId)),
				Object.class
		);
	}

	public ResponseEntity<Object> search(Long userId, String text, Integer from, Integer size) {
		String url = UriComponentsBuilder.fromHttpUrl(serverUrl + "/items/search")
				.queryParam("text", text)
				.queryParam("from", from)
				.queryParam("size", size)
				.toUriString();

		return restTemplate.exchange(
				url,
				HttpMethod.GET,
				new HttpEntity<>(headers(userId)),
				Object.class
		);
	}

	private HttpHeaders headers(Long userId) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("X-Sharer-User-Id", String.valueOf(userId));
		return headers;
	}
}