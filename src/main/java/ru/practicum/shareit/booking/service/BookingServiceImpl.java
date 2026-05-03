package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
	private final BookingRepository bookingRepository;
	private final ItemRepository itemRepository;
	private final UserService userService;

	@Override
	public BookingDto create(BookingCreateDto bookingCreateDto, Long userId) {
		if (!bookingCreateDto.getEnd().isAfter(bookingCreateDto.getStart())) {
			throw new ValidationException("End date must be after start date");
		}

		User booker = userService.getUserById(userId);

		Item item = itemRepository.findById(bookingCreateDto.getItemId())
				.orElseThrow(() -> new NotFoundException("Item not found"));

		if (item.getOwner().getId().equals(userId)) {
			throw new NotFoundException("Owner cannot book own item");
		}

		if (!Boolean.TRUE.equals(item.getAvailable())) {
			throw new ValidationException("Item is not available");
		}

		Booking booking = new Booking();
		booking.setStart(bookingCreateDto.getStart());
		booking.setEnd(bookingCreateDto.getEnd());
		booking.setItem(item);
		booking.setBooker(booker);
		booking.setStatus(BookingStatus.WAITING);

		return BookingMapper.toDto(bookingRepository.save(booking));
	}

	@Override
	public BookingDto approve(Long bookingId, Long userId, Boolean approved) {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new NotFoundException("Booking not found"));

		if (!booking.getItem().getOwner().getId().equals(userId)) {
			throw new NotFoundException("Only owner can approve booking");
		}

		if (booking.getStatus() != BookingStatus.WAITING) {
			throw new ConflictException("Booking already processed");
		}

		booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

		return BookingMapper.toDto(bookingRepository.save(booking));
	}

	@Override
	public BookingDto getById(Long bookingId, Long userId) {
		userService.getUserById(userId);

		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new NotFoundException("Booking not found"));

		Long bookerId = booking.getBooker().getId();
		Long ownerId = booking.getItem().getOwner().getId();

		if (!bookerId.equals(userId) && !ownerId.equals(userId)) {
			throw new NotFoundException("Booking is not available for this user");
		}

		return BookingMapper.toDto(booking);
	}

	@Override
	public List<BookingDto> getAllByUser(Long userId, BookingState state, int from, int size) {
		userService.getUserById(userId);

		PageRequest pageRequest = createPageRequest(from, size);
		List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageRequest);

		return filterBookings(bookings, state);
	}

	@Override
	public List<BookingDto> getAllByOwner(Long userId, BookingState state, int from, int size) {
		userService.getUserById(userId);

		PageRequest pageRequest = createPageRequest(from, size);
		List<Booking> bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId, pageRequest);

		return filterBookings(bookings, state);
	}

	private PageRequest createPageRequest(int from, int size) {
		if (from < 0 || size <= 0) {
			throw new ValidationException("Invalid pagination parameters");
		}

		return PageRequest.of(from / size, size);
	}

	private List<BookingDto> filterBookings(List<Booking> bookings, BookingState state) {
		LocalDateTime now = LocalDateTime.now();

		return bookings.stream()
				.filter(booking -> {
					switch (state) {
						case CURRENT:
							return booking.getStart().isBefore(now) && booking.getEnd().isAfter(now);
						case PAST:
							return booking.getEnd().isBefore(now);
						case FUTURE:
							return booking.getStart().isAfter(now);
						case WAITING:
							return booking.getStatus() == BookingStatus.WAITING;
						case REJECTED:
							return booking.getStatus() == BookingStatus.REJECTED;
						case ALL:
						default:
							return true;
					}
				})
				.map(BookingMapper::toDto)
				.toList();
	}
}