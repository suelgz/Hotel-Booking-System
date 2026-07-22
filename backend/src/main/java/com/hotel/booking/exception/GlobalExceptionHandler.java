package com.hotel.booking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage() == null ? "Invalid request data." : error.getDefaultMessage())
                .orElse("Invalid request data.");
        return ResponseEntity.badRequest().body(Map.of("error", message));
    }

    @ExceptionHandler({
            InvalidCustomerDataException.class,
            InvalidReservationDataException.class,
            InvalidRoomDataException.class,
            RoomNotAvailableException.class,
            IllegalArgumentException.class,
            DateTimeParseException.class,
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<Map<String, String>> handleBadRequest(Exception ex) {
        String message;
        if (ex instanceof DateTimeParseException || ex instanceof HttpMessageNotReadableException) {
            message = "Dates must use YYYY-MM-DD format and request fields must be valid.";
        } else if (ex instanceof MissingServletRequestParameterException parameterException) {
            message = parameterException.getParameterName() + " is required.";
        } else {
            message = ex.getMessage();
        }
        return ResponseEntity.badRequest().body(Map.of("error", message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUnexpected(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Something went wrong on the server."));
    }
}
