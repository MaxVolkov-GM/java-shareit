package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

	@Null(groups = Create.class)
	@NotNull(groups = Update.class)
	private Long id;

	@NotNull(groups = Create.class)
	private LocalDateTime start;

	@NotNull(groups = Create.class)
	private LocalDateTime end;

	@NotNull(groups = Create.class)
	private Long itemId;
}