package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

	boolean existsByItemIdAndBookerIdAndStatusAndEndBefore(Long itemId, Long bookerId, BookingStatus status,
	                                                       LocalDateTime now);

	Booking findFirstByItemIdAndStatusAndEndBefore(Long itemId, BookingStatus status, LocalDateTime now, Sort sort);

	Booking findFirstByItemIdAndStatusAndStartAfter(Long itemId, BookingStatus status, LocalDateTime now, Sort sort);

	List<Booking> findByBookerId(Long bookerId, Sort sort);

	List<Booking> findByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime start, LocalDateTime end,
	                                                      Sort sort);

	List<Booking> findByBookerIdAndEndBefore(Long bookerId, LocalDateTime now, Sort sort);

	List<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime now, Sort sort);

	List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

	List<Booking> findByItemOwnerId(Long ownerId, Sort sort);

	List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfter(Long ownerId, LocalDateTime start, LocalDateTime end,
	                                                         Sort sort);

	List<Booking> findByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime now, Sort sort);

	List<Booking> findByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime now, Sort sort);

	List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);
}