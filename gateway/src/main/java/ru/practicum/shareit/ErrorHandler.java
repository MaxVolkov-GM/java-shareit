package ru.practicum.shareit;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.error.ErrorResponse;

@RestControllerAdvice
public class ErrorHandler {

	@ExceptionHandler({
			MethodArgumentNotValidException.class,
			ConstraintViolationException.class,
			IllegalArgumentException.class,
			MissingRequestHeaderException.class
	})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleBadRequest(final Exception e) {
		return new ErrorResponse(e.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handleThrowable(final Throwable e) {
		return new ErrorResponse("Internal server error: " + e.getMessage());
	}
}