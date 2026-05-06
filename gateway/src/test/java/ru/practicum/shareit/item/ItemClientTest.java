package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ItemClientTest {

	private static final String SERVER_URL = "http://localhost:9090";
	private static final String USER_ID_HEADER = "X-Sharer-User-Id";

	private ItemClient itemClient;
	private MockRestServiceServer mockServer;

	@BeforeEach
	void setUp() {
		itemClient = new ItemClient(SERVER_URL);
		RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(itemClient, "rest");
		mockServer = MockRestServiceServer.bindTo(restTemplate).build();
	}

	@Test
	void create_shouldSendPostRequest() {
		ItemDto itemDto = new ItemDto(null, "Item", "Description", true, null);

		mockServer.expect(once(), requestTo(SERVER_URL + "/items"))
				.andExpect(method(HttpMethod.POST))
				.andExpect(header(USER_ID_HEADER, "1"))
				.andExpect(content().json("""
						{
							"name": "Item",
							"description": "Description",
							"available": true,
							"requestId": null
						}
						"""))
				.andRespond(withSuccess("""
						{
							"id": 1,
							"name": "Item",
							"description": "Description",
							"available": true,
							"requestId": null
						}
						""", MediaType.APPLICATION_JSON));

		assertEquals(HttpStatus.OK, itemClient.create(1L, itemDto).getStatusCode());
		mockServer.verify();
	}

	@Test
	void update_shouldSendPatchRequest() {
		ItemDto itemDto = new ItemDto(1L, "Updated", "Updated description", false, null);

		mockServer.expect(once(), requestTo(SERVER_URL + "/items/1"))
				.andExpect(method(HttpMethod.PATCH))
				.andExpect(header(USER_ID_HEADER, "1"))
				.andExpect(content().json("""
						{
							"id": 1,
							"name": "Updated",
							"description": "Updated description",
							"available": false,
							"requestId": null
						}
						"""))
				.andRespond(withSuccess("""
						{
							"id": 1,
							"name": "Updated",
							"description": "Updated description",
							"available": false,
							"requestId": null
						}
						""", MediaType.APPLICATION_JSON));

		assertEquals(HttpStatus.OK, itemClient.update(1L, 1L, itemDto).getStatusCode());
		mockServer.verify();
	}

	@Test
	void getById_shouldSendGetRequest() {
		mockServer.expect(once(), requestTo(SERVER_URL + "/items/1"))
				.andExpect(method(HttpMethod.GET))
				.andExpect(header(USER_ID_HEADER, "1"))
				.andRespond(withSuccess("""
						{
							"id": 1,
							"name": "Item",
							"description": "Description",
							"available": true,
							"requestId": null
						}
						""", MediaType.APPLICATION_JSON));

		assertEquals(HttpStatus.OK, itemClient.getById(1L, 1L).getStatusCode());
		mockServer.verify();
	}

	@Test
	void getUserItems_shouldSendGetRequest() {
		mockServer.expect(once(), requestTo(SERVER_URL + "/items?from=0&size=10"))
				.andExpect(method(HttpMethod.GET))
				.andExpect(header(USER_ID_HEADER, "1"))
				.andRespond(withSuccess("""
						[
							{
								"id": 1,
								"name": "Item",
								"description": "Description",
								"available": true,
								"requestId": null
							}
						]
						""", MediaType.APPLICATION_JSON));

		assertEquals(HttpStatus.OK, itemClient.getUserItems(1L, 0, 10).getStatusCode());
		mockServer.verify();
	}

	@Test
	void search_shouldSendGetRequest() {
		mockServer.expect(once(), requestTo(SERVER_URL + "/items/search?text=drill&from=0&size=10"))
				.andExpect(method(HttpMethod.GET))
				.andExpect(header(USER_ID_HEADER, "1"))
				.andRespond(withSuccess("""
						[
							{
								"id": 1,
								"name": "Drill",
								"description": "Powerful drill",
								"available": true,
								"requestId": null
							}
						]
						""", MediaType.APPLICATION_JSON));

		assertEquals(HttpStatus.OK, itemClient.search(1L, "drill", 0, 10).getStatusCode());
		mockServer.verify();
	}

	@Test
	void addComment_shouldSendPostRequest() {
		CommentDto commentDto = new CommentDto(null, "Good item", null, null);

		mockServer.expect(once(), requestTo(SERVER_URL + "/items/1/comment"))
				.andExpect(method(HttpMethod.POST))
				.andExpect(header(USER_ID_HEADER, "1"))
				.andExpect(content().json("""
						{
							"text": "Good item"
						}
						"""))
				.andRespond(withSuccess("""
						{
							"id": 1,
							"text": "Good item",
							"authorName": "User",
							"created": "2026-05-06T15:00:00"
						}
						""", MediaType.APPLICATION_JSON));

		assertEquals(HttpStatus.OK, itemClient.addComment(1L, 1L, commentDto).getStatusCode());
		mockServer.verify();
	}
}