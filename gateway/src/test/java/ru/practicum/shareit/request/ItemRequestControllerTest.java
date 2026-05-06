package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper mapper;

	@MockBean
	private ItemRequestClient itemRequestClient;

	@Test
	void create_shouldReturnOk() throws Exception {
		ItemRequestDto dto = new ItemRequestDto();
		dto.setDescription("Need item");

		when(itemRequestClient.create(eq(1L), any(ItemRequestDto.class)))
				.thenReturn(ResponseEntity.status(OK).build());

		mvc.perform(post("/requests")
						.header("X-Sharer-User-Id", 1)
						.contentType(APPLICATION_JSON)
						.content(mapper.writeValueAsString(dto)))
				.andExpect(status().isOk());

		verify(itemRequestClient).create(eq(1L), any(ItemRequestDto.class));
	}

	@Test
	void create_whenDescriptionBlank_shouldReturnBadRequest() throws Exception {
		ItemRequestDto dto = new ItemRequestDto();
		dto.setDescription("");

		mvc.perform(post("/requests")
						.header("X-Sharer-User-Id", 1)
						.contentType(APPLICATION_JSON)
						.content(mapper.writeValueAsString(dto)))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(itemRequestClient);
	}

	@Test
	void create_whenDescriptionTooLong_shouldReturnBadRequest() throws Exception {
		ItemRequestDto dto = new ItemRequestDto();
		dto.setDescription("a".repeat(1025));

		mvc.perform(post("/requests")
						.header("X-Sharer-User-Id", 1)
						.contentType(APPLICATION_JSON)
						.content(mapper.writeValueAsString(dto)))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(itemRequestClient);
	}

	@Test
	void getUserRequests_shouldReturnOk() throws Exception {
		when(itemRequestClient.getUserRequests(1L))
				.thenReturn(ResponseEntity.status(OK).build());

		mvc.perform(get("/requests")
						.header("X-Sharer-User-Id", 1))
				.andExpect(status().isOk());

		verify(itemRequestClient).getUserRequests(1L);
	}

	@Test
	void getAllRequests_shouldReturnOk() throws Exception {
		when(itemRequestClient.getAllRequests(1L))
				.thenReturn(ResponseEntity.status(OK).build());

		mvc.perform(get("/requests/all")
						.header("X-Sharer-User-Id", 1))
				.andExpect(status().isOk());

		verify(itemRequestClient).getAllRequests(1L);
	}

	@Test
	void getById_shouldReturnOk() throws Exception {
		when(itemRequestClient.getById(1L, 1L))
				.thenReturn(ResponseEntity.status(OK).build());

		mvc.perform(get("/requests/1")
						.header("X-Sharer-User-Id", 1))
				.andExpect(status().isOk());

		verify(itemRequestClient).getById(1L, 1L);
	}
}