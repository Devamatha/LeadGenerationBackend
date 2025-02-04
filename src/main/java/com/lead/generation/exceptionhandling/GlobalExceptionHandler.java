package com.lead.generation.exceptionhandling;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private Map<String, Object> createErrorResponse(HttpStatus status, String message, String path) {
		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", status.value());
		body.put("error", status.getReasonPhrase());
		body.put("message", message);
		body.put("path", path);
		return body;
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
		Map<String, Object> body = createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(),
				request.getDescription(false).replace("uri=", ""));
		return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<Object> handleNotFoundException(NoHandlerFoundException ex, HttpServletRequest request) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.NOT_FOUND.value());
		body.put("error", "Not Found");
		body.put("message", "API URL not found");
		body.put("path", request.getRequestURI());

		return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
	}

	// Handle 405 Method Not Allowed
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<Object> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex,
			HttpServletRequest request) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.METHOD_NOT_ALLOWED.value());
		body.put("error", "Method Not Allowed");
		body.put("message", "This HTTP method is not allowed for the requested URL");
		body.put("path", request.getRequestURI());

		return new ResponseEntity<>(body, HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
			WebRequest request) {
		Map<String, Object> body = createErrorResponse(HttpStatus.BAD_REQUEST,
				"Missing parameter: " + ex.getParameterName(), request.getDescription(false).replace("uri=", ""));
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
		Map<String, Object> body = createErrorResponse(HttpStatus.BAD_REQUEST,
				"Validation error: " + ex.getBindingResult().getFieldError().getDefaultMessage(),
				request.getDescription(false).replace("uri=", ""));
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MissingPathVariableException.class)
	public ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, WebRequest request) {
		Map<String, Object> body = createErrorResponse(HttpStatus.BAD_REQUEST,
				"Missing path variable: " + ex.getVariableName(), request.getDescription(false).replace("uri=", ""));
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Object> handleRuntimeException(RuntimeException ex, WebRequest request) {

		Map<String, Object> body = createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(),
				request.getDescription(false).replace("uri=", ""));
//        return ResponseEntity
//                .status(HttpStatus.PAYMENT_REQUIRED)
//                .body(ex.getMessage());
		return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex,
			WebRequest request) {
		String userFriendlyMessage = "A user with the same email already exists. Please use a different email.";

		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.CONFLICT.value()); // Use 409 Conflict
		body.put("error", "Conflict");
		body.put("message", ex.getMessage());
		body.put("details", ex.getMostSpecificCause().getMessage()); // For debugging (optional)
		body.put("path", request.getDescription(false).replace("uri=", ""));

		return new ResponseEntity<>(body, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<Map<String, Object>> handleExpiredJwtException(ExpiredJwtException ex) {
		return buildErrorResponse(HttpStatus.EXPECTATION_FAILED, "JWT is expired", ex.getMessage(),
				"/api/uthista/login");
	}

	private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String error, String message,
			String path) {
		Map<String, Object> errorDetails = new HashMap<>();
		errorDetails.put("timestamp", LocalDateTime.now().toString());
		errorDetails.put("status", status.value());
		errorDetails.put("error", error);
		errorDetails.put("message", message);
		errorDetails.put("path", path);

		return new ResponseEntity<>(errorDetails, status);
	}

}