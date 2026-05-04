package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

	private static final String USER_ID_HEADER = "X-Sharer-User-Id";

	private final ItemRequestService itemRequestService;

	@PostMapping
	public ItemRequestDto create(@RequestBody ItemRequestDto requestDto,
	                             @RequestHeader(USER_ID_HEADER) Long userId) {
		return itemRequestService.create(requestDto, userId);
	}

	@GetMapping
	public List<ItemRequestDto> getOwnRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
		return itemRequestService.getOwnRequests(userId);
	}

	@GetMapping("/all")
	public List<ItemRequestDto> getAllRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
		return itemRequestService.getAllRequests(userId);
	}

	@GetMapping("/{requestId}")
	public ItemRequestDto getById(@PathVariable Long requestId,
	                              @RequestHeader(USER_ID_HEADER) Long userId) {
		return itemRequestService.getById(requestId, userId);
	}
}