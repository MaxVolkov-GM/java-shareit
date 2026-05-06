package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRequest;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

	private static final Sort CREATED_DESC = Sort.by(Sort.Direction.DESC, "created");

	private final ItemRequestRepository itemRequestRepository;
	private final UserRepository userRepository;
	private final ItemRepository itemRepository;

	@Override
	public ItemRequestDto create(Long userId, ItemRequestDto dto) {
		User requestor = userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found"));

		ItemRequest request = ItemRequestMapper.toEntity(dto, requestor);
		ItemRequest savedRequest = itemRequestRepository.save(request);

		return ItemRequestMapper.toDto(savedRequest, List.of());
	}

	@Override
	public List<ItemRequestDto> getUserRequests(Long userId) {
		checkUserExists(userId);

		return itemRequestRepository.findByRequestorId(userId, CREATED_DESC).stream()
				.map(request -> ItemRequestMapper.toDto(request, getItemsForRequest(request.getId())))
				.toList();
	}

	@Override
	public List<ItemRequestDto> getAllRequests(Long userId) {
		checkUserExists(userId);

		return itemRequestRepository.findByRequestorIdNot(userId, CREATED_DESC).stream()
				.map(request -> ItemRequestMapper.toDto(request, getItemsForRequest(request.getId())))
				.toList();
	}

	@Override
	public ItemRequestDto getById(Long userId, Long requestId) {
		checkUserExists(userId);

		ItemRequest request = itemRequestRepository.findById(requestId)
				.orElseThrow(() -> new NotFoundException("Request not found"));

		return ItemRequestMapper.toDto(request, getItemsForRequest(requestId));
	}

	private void checkUserExists(Long userId) {
		if (!userRepository.existsById(userId)) {
			throw new NotFoundException("User not found");
		}
	}

	private List<Item> getItemsForRequest(Long requestId) {
		return itemRepository.findByRequestId(requestId);
	}
}