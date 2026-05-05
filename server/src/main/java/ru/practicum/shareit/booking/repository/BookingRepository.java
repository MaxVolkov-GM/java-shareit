package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	boolean existsByItemIdAndBookerIdAndStatusAndEndBefore(Long itemId, Long bookerId, BookingStatus status, LocalDateTime now);

	Booking findFirstByItemIdAndStatusAndEndBeforeOrderByStartDesc(Long itemId, BookingStatus status, LocalDateTime now);

	Booking findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(Long itemId, BookingStatus status, LocalDateTime now);

	List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

	List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId,
	                                                                      LocalDateTime start,
	                                                                      LocalDateTime end);

	List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

	List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

	List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

	List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

	List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId,
	                                                                         LocalDateTime start,
	                                                                         LocalDateTime end);

	List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime now);

	List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime now);

	List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);
}
