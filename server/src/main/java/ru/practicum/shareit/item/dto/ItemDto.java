package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

	private Long id;

	@NotBlank
	private String name;

	@NotBlank
	private String description;

	@NotNull
	private Boolean available;

	private Long requestId;

	private BookingShortDto lastBooking;

	private BookingShortDto nextBooking;

	private List<CommentDto> comments;

	public ItemDto(Long id, String name, String description, Boolean available, Long requestId) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.available = available;
		this.requestId = requestId;
	}
}