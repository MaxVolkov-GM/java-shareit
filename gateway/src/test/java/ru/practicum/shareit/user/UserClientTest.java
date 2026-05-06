package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class UserClientTest {

	private static final String SERVER_URL = "http://localhost:9090";

	private UserClient userClient;
	private MockRestServiceServer mockServer;

	@BeforeEach
	void setUp() {
		userClient = new UserClient(SERVER_URL);
		RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(userClient, "rest");
		mockServer = MockRestServiceServer.bindTo(restTemplate).build();
	}

	@Test
	void create_shouldSendPostRequest() {
		UserDto userDto = new UserDto(null, "User", "user@mail.com");

		mockServer.expect(once(), requestTo(SERVER_URL + "/users"))
				.andExpect(method(HttpMethod.POST))
				.andExpect(content().json("""
						{
							"name": "User",
							"email": "user@mail.com"
						}
						"""))
				.andRespond(withSuccess("""
						{
							"id": 1,
							"name": "User",
							"email": "user@mail.com"
						}
						""", MediaType.APPLICATION_JSON));

		assertEquals(HttpStatus.OK, userClient.create(userDto).getStatusCode());
		mockServer.verify();
	}

	@Test
	void update_shouldSendPatchRequest() {
		UserUpdateDto userDto = new UserUpdateDto(1L, "Updated", "updated@mail.com");

		mockServer.expect(once(), requestTo(SERVER_URL + "/users/1"))
				.andExpect(method(HttpMethod.PATCH))
				.andExpect(content().json("""
						{
							"id": 1,
							"name": "Updated",
							"email": "updated@mail.com"
						}
						"""))
				.andRespond(withSuccess("""
						{
							"id": 1,
							"name": "Updated",
							"email": "updated@mail.com"
						}
						""", MediaType.APPLICATION_JSON));

		assertEquals(HttpStatus.OK, userClient.update(1L, userDto).getStatusCode());
		mockServer.verify();
	}

	@Test
	void getById_shouldSendGetRequest() {
		mockServer.expect(once(), requestTo(SERVER_URL + "/users/1"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess("""
						{
							"id": 1,
							"name": "User",
							"email": "user@mail.com"
						}
						""", MediaType.APPLICATION_JSON));

		assertEquals(HttpStatus.OK, userClient.getById(1L).getStatusCode());
		mockServer.verify();
	}

	@Test
	void getAll_shouldSendGetRequest() {
		mockServer.expect(once(), requestTo(SERVER_URL + "/users"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess("""
						[
							{
								"id": 1,
								"name": "User",
								"email": "user@mail.com"
							}
						]
						""", MediaType.APPLICATION_JSON));

		assertEquals(HttpStatus.OK, userClient.getAll().getStatusCode());
		mockServer.verify();
	}

	@Test
	void delete_shouldSendDeleteRequest() {
		mockServer.expect(once(), requestTo(SERVER_URL + "/users/1"))
				.andExpect(method(HttpMethod.DELETE))
				.andRespond(withSuccess());

		assertEquals(HttpStatus.OK, userClient.delete(1L).getStatusCode());
		mockServer.verify();
	}
}