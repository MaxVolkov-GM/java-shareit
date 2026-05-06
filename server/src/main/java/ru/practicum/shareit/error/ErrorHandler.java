package ru.practicum.shareit.error;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

@RestControllerAdvice
public class ErrorHandler {

	@ExceptionHandler({
			IllegalArgumentException.class,
			ValidationException.class
	})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleBadRequest(RuntimeException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ExceptionHandler({
			IllegalStateException.class,
			ConflictException.class,
			DataIntegrityViolationException.class
	})
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse handleConflict(RuntimeException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleNotFound(RuntimeException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ExceptionHandler(ForbiddenException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorResponse handleForbidden(RuntimeException e) {
		return new ErrorResponse(e.getMessage());
	}
}