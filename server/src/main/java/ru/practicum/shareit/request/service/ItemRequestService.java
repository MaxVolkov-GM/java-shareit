package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

	ItemRequestDto create(ItemRequestDto requestDto, Long userId);

	List<ItemRequestDto> getOwnRequests(Long userId);

	List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size);

	ItemRequestDto getById(Long requestId, Long userId);
}