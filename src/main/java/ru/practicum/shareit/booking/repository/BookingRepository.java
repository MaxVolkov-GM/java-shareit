package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

	List<Booking> findAllByBookerId(Long userId, Pageable pageable);

	List<Booking> findAllByItemOwnerId(Long ownerId, Pageable pageable);

	List<Booking> findAllByBookerIdAndStatus(Long userId, BookingStatus status, Pageable pageable);

	List<Booking> findAllByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Pageable pageable);

	@Query("select b " +
			"from Booking b " +
			"where b.booker.id = ?1 " +
			"and ?2 between b.start and b.end")
	List<Booking> findCurrentByBookerId(Long userId, LocalDateTime date, Pageable pageable);

	@Query("select b " +
			"from Booking b " +
			"where b.item.owner.id = ?1 " +
			"and ?2 between b.start and b.end")
	List<Booking> findCurrentByOwnerId(Long ownerId, LocalDateTime date, Pageable pageable);

	List<Booking> findAllByBookerIdAndEndBefore(Long userId, LocalDateTime date, Pageable pageable);

	List<Booking> findAllByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime date, Pageable pageable);

	List<Booking> findAllByBookerIdAndStartAfter(Long userId, LocalDateTime date, Pageable pageable);

	List<Booking> findAllByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime date, Pageable pageable);

	boolean existsByItemIdAndBookerIdAndStatusAndEndBefore(
			Long itemId,
			Long bookerId,
			BookingStatus status,
			LocalDateTime end
	);

	Booking findFirstByItemIdAndStatusAndEndBefore(
			Long itemId,
			BookingStatus status,
			LocalDateTime now,
			Sort sort
	);

	Booking findFirstByItemIdAndStatusAndStartAfter(
			Long itemId,
			BookingStatus status,
			LocalDateTime now,
			Sort sort
	);

	List<Booking> findAllByItemIdInAndStatusAndEndBefore(
			Collection<Long> itemIds,
			BookingStatus status,
			LocalDateTime now,
			Sort sort
	);

	List<Booking> findAllByItemIdInAndStatusAndStartAfter(
			Collection<Long> itemIds,
			BookingStatus status,
			LocalDateTime now,
			Sort sort
	);
}