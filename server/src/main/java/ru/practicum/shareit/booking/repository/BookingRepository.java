package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
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

	List<Booking> findByBookerId(Long bookerId, Pageable pageable);

	List<Booking> findByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime start, LocalDateTime end,
	                                                      Pageable pageable);

	List<Booking> findByBookerIdAndEndBefore(Long bookerId, LocalDateTime now, Pageable pageable);

	List<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime now, Pageable pageable);

	List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

	List<Booking> findByItemOwnerId(Long ownerId, Pageable pageable);

	List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfter(Long ownerId, LocalDateTime start, LocalDateTime end,
	                                                         Pageable pageable);

	List<Booking> findByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime now, Pageable pageable);

	List<Booking> findByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime now, Pageable pageable);

	List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Pageable pageable);
}