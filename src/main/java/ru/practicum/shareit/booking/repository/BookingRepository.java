package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

	List<Booking> findAllByBookerIdOrderByStartDesc(Long userId, Pageable pageable);

	List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId, Pageable pageable);

	boolean existsByItemIdAndBookerIdAndStatusAndEndBefore(
			Long itemId,
			Long bookerId,
			BookingStatus status,
			LocalDateTime end
	);

	Booking findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(
			Long itemId,
			BookingStatus status,
			LocalDateTime now
	);

	Booking findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
			Long itemId,
			BookingStatus status,
			LocalDateTime now
	);
}