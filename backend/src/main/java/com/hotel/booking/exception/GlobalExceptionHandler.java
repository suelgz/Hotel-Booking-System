package com.hotel.booking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            InvalidCustomerDataException.class,
            InvalidReservationDataException.class,
            InvalidRoomDataException.class,
            RoomNotAvailableException.class,
            IllegalArgumentException.class,
            DateTimeParseException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<Map<String, String>> handleBadRequest(Exception ex) {
        String message;
        if (ex instanceof DateTimeParseException) {
            message = "Dates must use YYYY-MM-DD format.";
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
