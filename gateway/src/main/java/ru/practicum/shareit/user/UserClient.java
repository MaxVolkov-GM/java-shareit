package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

@Component
public class UserClient extends BaseClient {

	private static final String API_PREFIX = "/users";

	public UserClient(RestTemplate restTemplate,
	                  @Value("${shareit-server.url}") String serverUrl) {
		super(restTemplate, serverUrl);
	}

	public ResponseEntity<Object> create(UserDto userDto) {
		return post(API_PREFIX, null, userDto);
	}

	public ResponseEntity<Object> update(Long userId, UserDto userDto) {
		return patch(API_PREFIX + "/" + userId, null, userDto);
	}

	public ResponseEntity<Object> getById(Long userId) {
		return get(API_PREFIX + "/" + userId, null);
	}

	public ResponseEntity<Object> getAll() {
		return get(API_PREFIX, null);
	}

	public ResponseEntity<Object> deleteById(Long userId) {
		return delete(API_PREFIX + "/" + userId, null);
	}
}