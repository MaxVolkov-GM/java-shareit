package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String description;

	private Boolean available;

	@ManyToOne
	@JoinColumn(name = "owner_id")
	private ru.practicum.shareit.user.User owner;

	@ManyToOne
	@JoinColumn(name = "request_id")
	private ItemRequest request;
}