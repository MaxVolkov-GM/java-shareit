package ru.practicum.shareit.client;

import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class BaseClient {

	private final RestTemplate restTemplate;
	private final String serverUrl;

	public BaseClient(RestTemplate restTemplate, String serverUrl) {
		this.restTemplate = restTemplate;
		this.serverUrl = serverUrl;
	}

	protected ResponseEntity<Object> get(String path, Long userId) {
		HttpEntity<Void> requestEntity = new HttpEntity<>(headers(userId));
		return makeAndSendRequest(HttpMethod.GET, path, requestEntity);
	}

	protected ResponseEntity<Object> get(String path, Long userId, Map<String, Object> parameters) {
		HttpEntity<Void> requestEntity = new HttpEntity<>(headers(userId));
		return makeAndSendRequest(HttpMethod.GET, path, requestEntity, parameters);
	}

	protected ResponseEntity<Object> post(String path, Long userId, Object body) {
		HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers(userId));
		return makeAndSendRequest(HttpMethod.POST, path, requestEntity);
	}

	protected ResponseEntity<Object> patch(String path, Long userId, Object body) {
		HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers(userId));
		return makeAndSendRequest(HttpMethod.PATCH, path, requestEntity);
	}

	protected ResponseEntity<Object> patch(String path, Long userId, Object body, Map<String, Object> parameters) {
		HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers(userId));
		return makeAndSendRequest(HttpMethod.PATCH, path, requestEntity, parameters);
	}

	protected ResponseEntity<Object> delete(String path, Long userId) {
		HttpEntity<Void> requestEntity = new HttpEntity<>(headers(userId));
		return makeAndSendRequest(HttpMethod.DELETE, path, requestEntity);
	}

	private ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, HttpEntity<?> requestEntity) {
		try {
			return restTemplate.exchange(serverUrl + path, method, requestEntity, Object.class);
		} catch (HttpStatusCodeException e) {
			return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
		}
	}

	private ResponseEntity<Object> makeAndSendRequest(HttpMethod method,
	                                                  String path,
	                                                  HttpEntity<?> requestEntity,
	                                                  Map<String, Object> parameters) {
		try {
			return restTemplate.exchange(serverUrl + path, method, requestEntity, Object.class, parameters);
		} catch (HttpStatusCodeException e) {
			return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
		}
	}

	private HttpHeaders headers(Long userId) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		if (userId != null) {
			headers.set("X-Sharer-User-Id", String.valueOf(userId));
		}

		return headers;
	}
}