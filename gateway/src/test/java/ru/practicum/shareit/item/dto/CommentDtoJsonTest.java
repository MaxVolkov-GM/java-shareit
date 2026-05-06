package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {

	@Autowired
	private JacksonTester<CommentDto> json;

	@Test
	void serialize_shouldContainAllFields() throws Exception {
		CommentDto dto = new CommentDto(
				1L,
				"Good item",
				"User",
				"2026-05-06T16:00:00"
		);

		assertThat(json.write(dto)).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(json.write(dto)).extractingJsonPathStringValue("$.text").isEqualTo("Good item");
		assertThat(json.write(dto)).extractingJsonPathStringValue("$.authorName").isEqualTo("User");
		assertThat(json.write(dto)).extractingJsonPathStringValue("$.created").isEqualTo("2026-05-06T16:00:00");
	}

	@Test
	void deserialize_shouldReadAllFields() throws Exception {
		String content = """
				{
					"id": 1,
					"text": "Good item",
					"authorName": "User",
					"created": "2026-05-06T16:00:00"
				}
				""";

		CommentDto dto = json.parseObject(content);

		assertThat(dto.getId()).isEqualTo(1L);
		assertThat(dto.getText()).isEqualTo("Good item");
		assertThat(dto.getAuthorName()).isEqualTo("User");
		assertThat(dto.getCreated()).isEqualTo("2026-05-06T16:00:00");
	}
}