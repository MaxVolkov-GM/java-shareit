package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRequest;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.util.List;

public class ItemMapper {

	public static ItemDto toDto(Item item) {
		return toDto(item, null, null, List.of());
	}

	public static ItemDto toDto(Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments) {
		return new ItemDto(
				item.getId(),
				item.getName(),
				item.getDescription(),
				item.getAvailable(),
				item.getRequest() != null ? item.getRequest().getId() : null,
				toBookingShortDto(lastBooking),
				toBookingShortDto(nextBooking),
				comments.stream().map(ItemMapper::toCommentDto).toList()
		);
	}

	public static Item toItem(ItemDto itemDto) {
		Item item = new Item();
		item.setId(itemDto.getId());
		item.setName(itemDto.getName());
		item.setDescription(itemDto.getDescription());
		item.setAvailable(itemDto.getAvailable());

		if (itemDto.getRequestId() != null) {
			ItemRequest request = new ItemRequest();
			request.setId(itemDto.getRequestId());
			item.setRequest(request);
		}

		return item;
	}

	public static CommentDto toCommentDto(Comment comment) {
		return new CommentDto(
				comment.getId(),
				comment.getText(),
				comment.getAuthor().getName(),
				comment.getCreated()
		);
	}

	private static BookingShortDto toBookingShortDto(Booking booking) {
		if (booking == null) {
			return null;
		}
		return new BookingShortDto(booking.getId(), booking.getBooker().getId());
	}
}
