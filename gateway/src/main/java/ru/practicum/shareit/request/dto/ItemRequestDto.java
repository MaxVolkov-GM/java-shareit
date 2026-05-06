package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
	private Long id;

	@NotBlank
	@Size(max = 1024)
	private String description;

	private Long requestorId;
	private LocalDateTime created;
	private List<ItemShortDto> items;
}