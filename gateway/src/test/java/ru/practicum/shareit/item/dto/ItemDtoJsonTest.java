package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

	@Autowired
	private JacksonTester<ItemDto> json;

	@Test
	void serialize_shouldContainAllFields() throws Exception {
		ItemDto dto = new ItemDto(
				1L,
				"Drill",
				"Powerful drill",
				true,
				10L
		);

		assertThat(json.write(dto)).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(json.write(dto)).extractingJsonPathStringValue("$.name").isEqualTo("Drill");
		assertThat(json.write(dto)).extractingJsonPathStringValue("$.description")
				.isEqualTo("Powerful drill");
		assertThat(json.write(dto)).extractingJsonPathBooleanValue("$.available")
				.isTrue();
		assertThat(json.write(dto)).extractingJsonPathNumberValue("$.requestId")
				.isEqualTo(10);
	}

	@Test
	void deserialize_shouldReadAllFields() throws Exception {
		String content = """
				{
					"id": 1,
					"name": "Drill",
					"description": "Powerful drill",
					"available": true,
					"requestId": 10
				}
				""";

		ItemDto dto = json.parseObject(content);

		assertThat(dto.getId()).isEqualTo(1L);
		assertThat(dto.getName()).isEqualTo("Drill");
		assertThat(dto.getDescription()).isEqualTo("Powerful drill");
		assertThat(dto.getAvailable()).isTrue();
		assertThat(dto.getRequestId()).isEqualTo(10L);
	}
}