package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

	@Autowired
	private JacksonTester<ItemRequestDto> json;

	@Test
	void serialize_shouldContainFields() throws Exception {
		ItemRequestDto dto = new ItemRequestDto();
		dto.setId(1L);
		dto.setDescription("Need drill");
		dto.setCreated(LocalDateTime.of(2026, 5, 6, 16, 0));

		assertThat(json.write(dto)).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(json.write(dto)).extractingJsonPathStringValue("$.description")
				.isEqualTo("Need drill");
		assertThat(json.write(dto)).extractingJsonPathStringValue("$.created")
				.startsWith("2026-05-06T16:00");
	}

	@Test
	void deserialize_shouldReadFields() throws Exception {
		String content = """
				{
					"id": 1,
					"description": "Need drill",
					"created": "2026-05-06T16:00:00"
				}
				""";

		ItemRequestDto dto = json.parseObject(content);

		assertThat(dto.getId()).isEqualTo(1L);
		assertThat(dto.getDescription()).isEqualTo("Need drill");
		assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2026, 5, 6, 16, 0));
	}
}