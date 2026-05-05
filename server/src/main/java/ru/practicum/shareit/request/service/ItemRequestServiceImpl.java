package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

	private final ItemRequestRepository requestRepository;
	private final UserRepository userRepository;
	private final ItemRepository itemRepository;

	@Override
	public ItemRequestDto create(Long userId, ItemRequestDto dto) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found"));

		ItemRequest request = ItemRequestMapper.toEntity(dto, user);
		ItemRequest saved = requestRepository.save(request);

		return ItemRequestMapper.toDto(saved, List.of());
	}

	@Override
	public List<ItemRequestDto> getUserRequests(Long userId) {
		checkUser(userId);

		List<ItemRequest> requests = requestRepository
				.findByRequestorIdOrderByCreatedDesc(userId);

		Map<Long, List<Item>> itemsMap = getItemsMap(requests);

		return requests.stream()
				.map(r -> ItemRequestMapper.toDto(r,
						itemsMap.getOrDefault(r.getId(), List.of())))
				.toList();
	}

	@Override
	public List<ItemRequestDto> getAllRequests(Long userId) {
		checkUser(userId);

		List<ItemRequest> requests = requestRepository
				.findByRequestorIdNotOrderByCreatedDesc(userId);

		Map<Long, List<Item>> itemsMap = getItemsMap(requests);

		return requests.stream()
				.map(r -> ItemRequestMapper.toDto(r,
						itemsMap.getOrDefault(r.getId(), List.of())))
				.toList();
	}

	@Override
	public ItemRequestDto getById(Long userId, Long requestId) {
		checkUser(userId);

		ItemRequest request = requestRepository.findById(requestId)
				.orElseThrow(() -> new NotFoundException("Request not found"));

		List<Item> items = itemRepository.findByRequestId(requestId);

		return ItemRequestMapper.toDto(request, items);
	}

	private void checkUser(Long userId) {
		userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found"));
	}

	private Map<Long, List<Item>> getItemsMap(List<ItemRequest> requests) {
		List<Long> requestIds = requests.stream()
				.map(ItemRequest::getId)
				.toList();

		return itemRepository.findByRequestIdIn(requestIds).stream()
				.collect(Collectors.groupingBy(
						item -> item.getRequest().getId()
				));
	}
}