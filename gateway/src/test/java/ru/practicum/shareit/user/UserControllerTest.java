package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserClient userClient;

	@Test
	void createUser_whenValidUser_thenStatusOk() throws Exception {
		UserDto userDto = new UserDto(null, "User", "user@mail.com");

		mockMvc.perform(post("/users")
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(userDto)))
				.andExpect(status().isOk());
	}

	@Test
	void createUser_whenInvalidEmail_thenStatusBadRequest() throws Exception {
		UserDto userDto = new UserDto(null, "User", "wrong-email");

		mockMvc.perform(post("/users")
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(userDto)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void createUser_whenBlankName_thenStatusBadRequest() throws Exception {
		UserDto userDto = new UserDto(null, "", "user@mail.com");

		mockMvc.perform(post("/users")
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(userDto)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void getAllUsers_thenStatusOk() throws Exception {
		mockMvc.perform(get("/users"))
				.andExpect(status().isOk());
	}

	@Test
	void getUserById_thenStatusOk() throws Exception {
		mockMvc.perform(get("/users/1"))
				.andExpect(status().isOk());
	}

	@Test
	void updateUser_whenValidUser_thenStatusOk() throws Exception {
		UserUpdateDto userDto = new UserUpdateDto(null, "New Name", "new@mail.com");

		mockMvc.perform(patch("/users/1")
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(userDto)))
				.andExpect(status().isOk());
	}

	@Test
	void updateUser_whenInvalidEmail_thenStatusBadRequest() throws Exception {
		UserUpdateDto userDto = new UserUpdateDto(null, "New Name", "wrong-email");

		mockMvc.perform(patch("/users/1")
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(userDto)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void deleteUser_thenStatusOk() throws Exception {
		mockMvc.perform(delete("/users/1"))
				.andExpect(status().isOk());
	}
}