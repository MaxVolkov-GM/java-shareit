package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ItemClient itemClient;

	@Test
	void createItem_whenValidItem_thenStatusOk() throws Exception {
		ItemDto itemDto = new ItemDto(null, "Дрель", "Аккумуляторная дрель", true, null);

		mockMvc.perform(post("/items")
						.header("X-Sharer-User-Id", 1L)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(itemDto)))
				.andExpect(status().isOk());

		verify(itemClient).create(1L, itemDto);
	}

	@Test
	void createItem_whenBlankName_thenStatusBadRequest() throws Exception {
		ItemDto itemDto = new ItemDto(null, "", "Аккумуляторная дрель", true, null);

		mockMvc.perform(post("/items")
						.header("X-Sharer-User-Id", 1L)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(itemDto)))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(itemClient);
	}

	@Test
	void createItem_whenNameTooLong_thenStatusBadRequest() throws Exception {
		ItemDto itemDto = new ItemDto(null, "a".repeat(256), "Аккумуляторная дрель", true, null);

		mockMvc.perform(post("/items")
						.header("X-Sharer-User-Id", 1L)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(itemDto)))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(itemClient);
	}

	@Test
	void createItem_whenBlankDescription_thenStatusBadRequest() throws Exception {
		ItemDto itemDto = new ItemDto(null, "Дрель", "", true, null);

		mockMvc.perform(post("/items")
						.header("X-Sharer-User-Id", 1L)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(itemDto)))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(itemClient);
	}

	@Test
	void createItem_whenDescriptionTooLong_thenStatusBadRequest() throws Exception {
		ItemDto itemDto = new ItemDto(null, "Дрель", "a".repeat(513), true, null);

		mockMvc.perform(post("/items")
						.header("X-Sharer-User-Id", 1L)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(itemDto)))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(itemClient);
	}

	@Test
	void createItem_whenAvailableIsNull_thenStatusBadRequest() throws Exception {
		ItemDto itemDto = new ItemDto(null, "Дрель", "Аккумуляторная дрель", null, null);

		mockMvc.perform(post("/items")
						.header("X-Sharer-User-Id", 1L)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(itemDto)))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(itemClient);
	}

	@Test
	void updateItem_whenValidItem_thenStatusOk() throws Exception {
		ItemDto itemDto = new ItemDto(null, "Дрель", "Новое описание", true, null);

		mockMvc.perform(patch("/items/1")
						.header("X-Sharer-User-Id", 1L)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(itemDto)))
				.andExpect(status().isOk());

		verify(itemClient).update(1L, 1L, itemDto);
	}

	@Test
	void updateItem_whenNameTooLong_thenStatusBadRequest() throws Exception {
		ItemDto itemDto = new ItemDto(null, "a".repeat(256), "Новое описание", true, null);

		mockMvc.perform(patch("/items/1")
						.header("X-Sharer-User-Id", 1L)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(itemDto)))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(itemClient);
	}

	@Test
	void updateItem_whenDescriptionTooLong_thenStatusBadRequest() throws Exception {
		ItemDto itemDto = new ItemDto(null, "Дрель", "a".repeat(513), true, null);

		mockMvc.perform(patch("/items/1")
						.header("X-Sharer-User-Id", 1L)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(itemDto)))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(itemClient);
	}

	@Test
	void getItemById_thenStatusOk() throws Exception {
		mockMvc.perform(get("/items/1")
						.header("X-Sharer-User-Id", 1L))
				.andExpect(status().isOk());

		verify(itemClient).getById(1L, 1L);
	}

	@Test
	void getUserItems_thenStatusOk() throws Exception {
		mockMvc.perform(get("/items")
						.header("X-Sharer-User-Id", 1L)
						.param("from", "0")
						.param("size", "10"))
				.andExpect(status().isOk());

		verify(itemClient).getUserItems(1L, 0, 10);
	}

	@Test
	void getUserItems_whenFromIsNegative_thenStatusBadRequest() throws Exception {
		mockMvc.perform(get("/items")
						.header("X-Sharer-User-Id", 1L)
						.param("from", "-1")
						.param("size", "10"))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(itemClient);
	}

	@Test
	void getUserItems_whenSizeIsZero_thenStatusBadRequest() throws Exception {
		mockMvc.perform(get("/items")
						.header("X-Sharer-User-Id", 1L)
						.param("from", "0")
						.param("size", "0"))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(itemClient);
	}

	@Test
	void searchItems_whenMissingUserHeader_thenStatusBadRequest() throws Exception {
		mockMvc.perform(get("/items/search")
						.param("text", "дрель")
						.param("from", "0")
						.param("size", "10"))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(itemClient);
	}

	@Test
	void searchItems_whenTextIsBlank_thenStatusOk() throws Exception {
		mockMvc.perform(get("/items/search")
						.header("X-Sharer-User-Id", 1L)
						.param("text", "")
						.param("from", "0")
						.param("size", "10"))
				.andExpect(status().isOk());

		verifyNoInteractions(itemClient);
	}

	@Test
	void searchItems_whenValidRequest_thenStatusOk() throws Exception {
		mockMvc.perform(get("/items/search")
						.header("X-Sharer-User-Id", 1L)
						.param("text", "дрель")
						.param("from", "0")
						.param("size", "10"))
				.andExpect(status().isOk());

		verify(itemClient).search(1L, "дрель", 0, 10);
	}
}