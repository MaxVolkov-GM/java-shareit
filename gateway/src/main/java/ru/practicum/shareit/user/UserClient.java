package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@Component
public class UserClient extends BaseClient {
	private static final String API_PREFIX = "/users";

	public UserClient(@Value("${shareit-server.url}") String serverUrl) {
		super(serverUrl);
	}

	public ResponseEntity<Object> create(UserDto userDto) {
		return post(API_PREFIX, userDto);
	}

	public ResponseEntity<Object> update(Long userId, UserUpdateDto userDto) {
		return patch(API_PREFIX + "/" + userId, userDto);
	}

	public ResponseEntity<Object> getById(Long userId) {
		return get(API_PREFIX + "/" + userId);
	}

	public ResponseEntity<Object> getAll() {
		return get(API_PREFIX);
	}

	public ResponseEntity<Object> delete(Long userId) {
		return makeAndSendRequest(HttpMethod.DELETE, API_PREFIX + "/" + userId, null, null, null);
	}
}