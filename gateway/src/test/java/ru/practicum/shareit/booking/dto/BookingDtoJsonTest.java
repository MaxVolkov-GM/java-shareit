package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

	@Autowired
	private JacksonTester<BookingDto> json;

	@Test
	void bookingDtoSerializationTest() throws Exception {
		BookingDto bookingDto = new BookingDto(
				1L,
				LocalDateTime.of(2026, 5, 6, 12, 0),
				LocalDateTime.of(2026, 5, 7, 12, 0),
				10L
		);

		assertThat(json.write(bookingDto)).hasJsonPathNumberValue("$.id");
		assertThat(json.write(bookingDto)).hasJsonPathStringValue("$.start");
		assertThat(json.write(bookingDto)).hasJsonPathStringValue("$.end");
		assertThat(json.write(bookingDto)).hasJsonPathNumberValue("$.itemId");

		assertThat(json.write(bookingDto)).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(json.write(bookingDto)).extractingJsonPathNumberValue("$.itemId").isEqualTo(10);
	}

	@Test
	void bookingDtoDeserializationTest() throws Exception {
		String content = """
				{
				  "id": 1,
				  "start": "2026-05-06T12:00:00",
				  "end": "2026-05-07T12:00:00",
				  "itemId": 10
				}
				""";

		BookingDto bookingDto = json.parseObject(content);

		assertThat(bookingDto.getId()).isEqualTo(1L);
		assertThat(bookingDto.getStart()).isEqualTo(LocalDateTime.of(2026, 5, 6, 12, 0));
		assertThat(bookingDto.getEnd()).isEqualTo(LocalDateTime.of(2026, 5, 7, 12, 0));
		assertThat(bookingDto.getItemId()).isEqualTo(10L);
	}
}