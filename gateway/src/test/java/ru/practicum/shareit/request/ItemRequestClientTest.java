package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ItemRequestClientTest {

	private static final String SERVER_URL = "http://localhost:9090";
	private static final String USER_ID_HEADER = "X-Sharer-User-Id";

	private ItemRequestClient itemRequestClient;
	private MockRestServiceServer mockServer;

	@BeforeEach
	void setUp() {
		itemRequestClient = new ItemRequestClient(SERVER_URL);
		RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(itemRequestClient, "rest");
		mockServer = MockRestServiceServer.bindTo(restTemplate).build();
	}

	@Test
	void create_shouldSendPostRequest() {
		ItemRequestDto requestDto = new ItemRequestDto();
		requestDto.setDescription("Need drill");

		mockServer.expect(once(), requestTo(SERVER_URL + "/requests"))
				.andExpect(method(HttpMethod.POST))
				.andExpect(header(USER_ID_HEADER, "1"))
				.andExpect(content().json("""
						{
							"description": "Need drill"
						}
						"""))
				.andRespond(withSuccess("""
						{
							"id": 1,
							"description": "Need drill"
						}
						""", MediaType.APPLICATION_JSON));

		assertEquals(HttpStatus.OK, itemRequestClient.create(1L, requestDto).getStatusCode());
		mockServer.verify();
	}

	@Test
	void getUserRequests_shouldSendGetRequest() {
		mockServer.expect(once(), requestTo(SERVER_URL + "/requests"))
				.andExpect(method(HttpMethod.GET))
				.andExpect(header(USER_ID_HEADER, "1"))
				.andRespond(withSuccess("""
						[
							{
								"id": 1,
								"description": "Need drill"
							}
						]
						""", MediaType.APPLICATION_JSON));

		assertEquals(HttpStatus.OK, itemRequestClient.getUserRequests(1L).getStatusCode());
		mockServer.verify();
	}

	@Test
	void getAllRequests_shouldSendGetRequest() {
		mockServer.expect(once(), requestTo(SERVER_URL + "/requests/all"))
				.andExpect(method(HttpMethod.GET))
				.andExpect(header(USER_ID_HEADER, "1"))
				.andRespond(withSuccess("""
						[
							{
								"id": 1,
								"description": "Need drill"
							}
						]
						""", MediaType.APPLICATION_JSON));

		assertEquals(HttpStatus.OK, itemRequestClient.getAllRequests(1L).getStatusCode());
		mockServer.verify();
	}

	@Test
	void getById_shouldSendGetRequest() {
		mockServer.expect(once(), requestTo(SERVER_URL + "/requests/1"))
				.andExpect(method(HttpMethod.GET))
				.andExpect(header(USER_ID_HEADER, "1"))
				.andRespond(withSuccess("""
						{
							"id": 1,
							"description": "Need drill"
						}
						""", MediaType.APPLICATION_JSON));

		assertEquals(HttpStatus.OK, itemRequestClient.getById(1L, 1L).getStatusCode());
		mockServer.verify();
	}
}