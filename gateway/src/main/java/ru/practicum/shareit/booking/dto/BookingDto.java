package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

	public interface Create {}

	private Long id;

	@NotNull(groups = Create.class)
	@FutureOrPresent(groups = Create.class)
	private LocalDateTime start;

	@NotNull(groups = Create.class)
	@Future(groups = Create.class)
	private LocalDateTime end;

	@NotNull(groups = Create.class)
	private Long itemId;
}