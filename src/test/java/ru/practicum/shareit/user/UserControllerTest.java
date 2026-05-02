package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void createUser() throws Exception {
		UserDto user = new UserDto(null, "Test User", "create-user@mail.com");

		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(user)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.name").value("Test User"))
				.andExpect(jsonPath("$.email").value("create-user@mail.com"));
	}

	@Test
	void getUser() throws Exception {
		UserDto user = new UserDto(null, "Test User", "get-user@mail.com");

		String response = mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(user)))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		UserDto created = objectMapper.readValue(response, UserDto.class);

		mockMvc.perform(get("/users/" + created.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(created.getId()))
				.andExpect(jsonPath("$.email").value("get-user@mail.com"));
	}
}