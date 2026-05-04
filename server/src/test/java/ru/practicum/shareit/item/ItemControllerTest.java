package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void createItem() throws Exception {
		UserDto user = new UserDto(null, "User", "item-user-create@mail.com");

		String userResponse = mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(user)))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		UserDto createdUser = objectMapper.readValue(userResponse, UserDto.class);

		ItemDto item = new ItemDto(null, "Дрель", "Описание", true, null);

		mockMvc.perform(post("/items")
						.header("X-Sharer-User-Id", createdUser.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(item)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.name").value("Дрель"));
	}

	@Test
	void searchItem() throws Exception {
		UserDto user = new UserDto(null, "User", "item-user-search@mail.com");

		String userResponse = mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(user)))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		UserDto createdUser = objectMapper.readValue(userResponse, UserDto.class);

		ItemDto item = new ItemDto(null, "Шуруповерт", "Описание", true, null);

		mockMvc.perform(post("/items")
						.header("X-Sharer-User-Id", createdUser.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(item)))
				.andExpect(status().isOk());

		mockMvc.perform(get("/items/search?text=шуруповерт"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name").value("Шуруповерт"));
	}
}