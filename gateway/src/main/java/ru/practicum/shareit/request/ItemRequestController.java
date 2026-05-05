package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
	private final ItemRequestClient itemRequestClient;

	@PostMapping
	public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                     @Valid @RequestBody ItemRequestDto requestDto) {
		return itemRequestClient.create(userId, requestDto);
	}

	@GetMapping
	public ResponseEntity<Object> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
		return itemRequestClient.getUserRequests(userId);
	}

	@GetMapping("/all")
	public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
		return itemRequestClient.getAllRequests(userId);
	}

	@GetMapping("/{requestId}")
	public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
	                                      @PathVariable Long requestId) {
		return itemRequestClient.getById(userId, requestId);
	}
}
