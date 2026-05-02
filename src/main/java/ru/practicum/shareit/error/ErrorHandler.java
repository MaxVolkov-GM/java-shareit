package ru.practicum.shareit.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> handleValidation(ValidationException e) {
		return Map.of("error", e.getMessage());
	}

	@ExceptionHandler(ConflictException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public Map<String, String> handleConflict(ConflictException e) {
		return Map.of("error", e.getMessage());
	}

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public Map<String, String> handleNotFound(NotFoundException e) {
		return Map.of("error", e.getMessage());
	}
}