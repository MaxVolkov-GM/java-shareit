package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

	private final ItemRequestRepository itemRequestRepository;
	private final ItemRepository itemRepository;
	private final UserService userService;

	@Override
	public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) {
		User requestor = userService.getUserById(userId);

		ItemRequest itemRequest = new ItemRequest();
		itemRequest.setDescription(itemRequestDto.getDescription());
		itemRequest.setRequestor(requestor);
		itemRequest.setCreated(LocalDateTime.now());

		return ItemRequestMapper.toDto(itemRequestRepository.save(itemRequest), List.of());
	}

	@Override
	public List<ItemRequestDto> getOwnRequests(Long userId) {
		userService.getUserById(userId);

		List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);

		return mapToDtoWithItems(requests);
	}

	@Override
	public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
		userService.getUserById(userId);

		PageRequest pageRequest = PageRequest.of(
				from / size,
				size,
				Sort.by(Sort.Direction.DESC, "created")
		);

		List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdNot(userId, pageRequest)
				.getContent();

		return mapToDtoWithItems(requests);
	}

	@Override
	public ItemRequestDto getById(Long requestId, Long userId) {
		userService.getUserById(userId);

		ItemRequest itemRequest = itemRequestRepository.findById(requestId)
				.orElseThrow(() -> new NotFoundException("Request not found"));

		List<Item> items = itemRepository.findAllByRequestId(requestId);

		return ItemRequestMapper.toDto(itemRequest, items);
	}

	private List<ItemRequestDto> mapToDtoWithItems(List<ItemRequest> requests) {
		if (requests.isEmpty()) {
			return List.of();
		}

		List<Long> requestIds = requests.stream()
				.map(ItemRequest::getId)
				.toList();

		Map<Long, List<Item>> itemsByRequestId = itemRepository.findAllByRequestIdIn(requestIds)
				.stream()
				.collect(Collectors.groupingBy(item -> item.getRequest().getId()));

		return requests.stream()
				.map(request -> ItemRequestMapper.toDto(
						request,
						itemsByRequestId.getOrDefault(request.getId(), List.of())
				))
				.toList();
	}
}