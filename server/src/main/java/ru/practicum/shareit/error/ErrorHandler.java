package ru.practicum.shareit.error;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

	@ExceptionHandler({
			IllegalArgumentException.class,
			ValidationException.class
	})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> handleBadRequest(RuntimeException e) {
		return Map.of("error", e.getMessage());
	}

	@ExceptionHandler({
			IllegalStateException.class,
			ConflictException.class,
			DataIntegrityViolationException.class
	})
	@ResponseStatus(HttpStatus.CONFLICT)
	public Map<String, String> handleConflict(RuntimeException e) {
		return Map.of("error", e.getMessage());
	}

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public Map<String, String> handleNotFound(RuntimeException e) {
		return Map.of("error", e.getMessage());
	}
}