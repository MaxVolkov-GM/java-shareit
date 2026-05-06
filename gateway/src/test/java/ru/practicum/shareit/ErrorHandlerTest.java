package ru.practicum.shareit;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.error.ErrorResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ErrorHandlerTest {

	private final ErrorHandler errorHandler = new ErrorHandler();

	@Test
	void handleBadRequest_shouldReturnErrorResponseForConstraintViolationException() {
		ErrorResponse response = errorHandler.handleBadRequest(
				new ConstraintViolationException("Constraint violation", null)
		);

		assertNotNull(response);
		assertEquals("Constraint violation", response.getError());
	}

	@Test
	void handleBadRequest_shouldReturnErrorResponseForIllegalArgumentException() {
		ErrorResponse response = errorHandler.handleBadRequest(
				new IllegalArgumentException("Bad request")
		);

		assertNotNull(response);
		assertEquals("Bad request", response.getError());
	}

	@Test
	void handleThrowable_shouldReturnErrorResponse() {
		ErrorResponse response = errorHandler.handleThrowable(
				new RuntimeException("Server error")
		);

		assertNotNull(response);
		assertEquals("Internal server error: Server error", response.getError());
	}
}