package ru.practicum.shareit.item.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemForRequestDto {

	private Long id;
	private String name;
	private Long ownerId;
}