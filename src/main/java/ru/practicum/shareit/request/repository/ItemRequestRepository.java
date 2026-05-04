package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

	List<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(Long userId);

	List<ItemRequest> findAllByRequestorIdNotOrderByCreatedDesc(Long userId);
}