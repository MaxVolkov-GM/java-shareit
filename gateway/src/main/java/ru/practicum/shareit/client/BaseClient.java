package ru.practicum.shareit.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Map;

public class BaseClient {
	protected final RestTemplate rest;

	public BaseClient(String serverUrl) {
		this.rest = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
		this.rest.setUriTemplateHandler(new DefaultUriBuilderFactory(serverUrl));
	}

	protected ResponseEntity<Object> get(String path) {
		return makeAndSendRequest(HttpMethod.GET, path, null, null, null);
	}

	protected ResponseEntity<Object> get(String path, Long userId) {
		return makeAndSendRequest(HttpMethod.GET, path, userId, null, null);
	}

	protected ResponseEntity<Object> get(String path, Long userId, Map<String, Object> parameters) {
		return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, null);
	}

	protected ResponseEntity<Object> post(String path, Object body) {
		return makeAndSendRequest(HttpMethod.POST, path, null, null, body);
	}

	protected ResponseEntity<Object> post(String path, Long userId, Object body) {
		return makeAndSendRequest(HttpMethod.POST, path, userId, null, body);
	}

	protected ResponseEntity<Object> patch(String path, Object body) {
		return makeAndSendRequest(HttpMethod.PATCH, path, null, null, body);
	}

	protected ResponseEntity<Object> patch(String path, Long userId, Object body) {
		return makeAndSendRequest(HttpMethod.PATCH, path, userId, null, body);
	}

	protected ResponseEntity<Object> delete(String path) {
		return makeAndSendRequest(HttpMethod.DELETE, path, null, null, null);
	}

	protected ResponseEntity<Object> makeAndSendRequest(HttpMethod method,
	                                                    String path,
	                                                    Long userId,
	                                                    Map<String, Object> parameters,
	                                                    Object body) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (userId != null) {
			headers.set("X-Sharer-User-Id", String.valueOf(userId));
		}
		HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);

		try {
			if (parameters != null) {
				return rest.exchange(path, method, requestEntity, Object.class, parameters);
			}
			return rest.exchange(path, method, requestEntity, Object.class);
		} catch (HttpStatusCodeException e) {
			return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
		}
	}
}
