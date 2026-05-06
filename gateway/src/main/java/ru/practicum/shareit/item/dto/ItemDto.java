package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

	@Null(groups = Create.class)
	private Long id;

	@NotBlank(groups = Create.class)
	@Size(max = 255, groups = {Create.class, Update.class})
	private String name;

	@NotBlank(groups = Create.class)
	@Size(max = 512, groups = {Create.class, Update.class})
	private String description;

	@NotNull(groups = Create.class)
	private Boolean available;

	private Long requestId;
}