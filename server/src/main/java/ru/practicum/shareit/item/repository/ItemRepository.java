package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

	List<Item> findByOwnerId(Long ownerId);

	List<Item> findByRequestId(Long requestId);

	List<Item> findByRequestIdIn(Collection<Long> requestIds);

	@Query("""
            select item
            from Item item
            where item.available = true
              and (
                    lower(item.name) like lower(concat('%', :text, '%'))
                    or lower(item.description) like lower(concat('%', :text, '%'))
              )
            """)
	List<Item> search(String text);
}