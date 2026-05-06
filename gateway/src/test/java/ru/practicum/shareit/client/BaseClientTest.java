package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class BaseClientTest {

	private static final String SERVER_URL = "http://localhost:9090";
	private static final String USER_ID_HEADER = "X-Sharer-User-Id";

	private TestClient client;
	private MockRestServiceServer mockServer;

	@BeforeEach
	void setUp() {
		client = new TestClient(SERVER_URL);
		RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(client, "rest");
		mockServer = MockRestServiceServer.bindTo(restTemplate).build();
	}

	@Test
	void getWithoutUserId_shouldReturnOk() {
		mockServer.expect(once(), requestTo(SERVER_URL + "/test"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

		assertEquals(HttpStatus.OK, client.getWithoutUserId().getStatusCode());
		mockServer.verify();
	}

	@Test
	void getWithUserId_shouldReturnOk() {
		mockServer.expect(once(), requestTo(SERVER_URL + "/test"))
				.andExpect(method(HttpMethod.GET))
				.andExpect(header(USER_ID_HEADER, "1"))
				.andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

		assertEquals(HttpStatus.OK, client.getWithUserId().getStatusCode());
		mockServer.verify();
	}

	@Test
	void getWithParameters_shouldReturnOk() {
		mockServer.expect(once(), requestTo(SERVER_URL + "/test?state=ALL"))
				.andExpect(method(HttpMethod.GET))
				.andExpect(header(USER_ID_HEADER, "1"))
				.andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

		assertEquals(HttpStatus.OK, client.getWithParameters().getStatusCode());
		mockServer.verify();
	}

	@Test
	void postWithoutUserId_shouldReturnOk() {
		mockServer.expect(once(), requestTo(SERVER_URL + "/test"))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

		assertEquals(HttpStatus.OK, client.postWithoutUserId().getStatusCode());
		mockServer.verify();
	}

	@Test
	void postWithUserId_shouldReturnOk() {
		mockServer.expect(once(), requestTo(SERVER_URL + "/test"))
				.andExpect(method(HttpMethod.POST))
				.andExpect(header(USER_ID_HEADER, "1"))
				.andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

		assertEquals(HttpStatus.OK, client.postWithUserId().getStatusCode());
		mockServer.verify();
	}

	@Test
	void patch_shouldReturnOk() {
		mockServer.expect(once(), requestTo(SERVER_URL + "/test"))
				.andExpect(method(HttpMethod.PATCH))
				.andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

		assertEquals(HttpStatus.OK, client.patchRequest().getStatusCode());
		mockServer.verify();
	}

	@Test
	void delete_shouldReturnOk() {
		mockServer.expect(once(), requestTo(SERVER_URL + "/test"))
				.andExpect(method(HttpMethod.DELETE))
				.andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

		assertEquals(HttpStatus.OK, client.deleteRequest().getStatusCode());
		mockServer.verify();
	}

	private static class TestClient extends BaseClient {

		TestClient(String serverUrl) {
			super(serverUrl);
		}

		org.springframework.http.ResponseEntity<Object> getWithoutUserId() {
			return get("/test");
		}

		org.springframework.http.ResponseEntity<Object> getWithUserId() {
			return get("/test", 1L);
		}

		org.springframework.http.ResponseEntity<Object> getWithParameters() {
			return get("/test?state={state}", 1L, Map.of("state", "ALL"));
		}

		org.springframework.http.ResponseEntity<Object> postWithoutUserId() {
			return post("/test", Map.of("name", "test"));
		}

		org.springframework.http.ResponseEntity<Object> postWithUserId() {
			return post("/test", 1L, Map.of("name", "test"));
		}

		org.springframework.http.ResponseEntity<Object> patchRequest() {
			return patch("/test", Map.of("name", "test"));
		}

		org.springframework.http.ResponseEntity<Object> deleteRequest() {
			return makeAndSendRequest(HttpMethod.DELETE, "/test", null, null, null);
		}
	}
}