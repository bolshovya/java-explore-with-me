package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestException(BadRequestException e) {
        return ApiError.builder()
                .status("BAD_REQUEST")
                .reason("Incorrectly made request.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(NotFoundException e) {
        return ApiError.builder()
                .status("NOT_FOUND")
                .reason("The required object was not found.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleForbiddenException(ForbiddenException e) {
        return ApiError.builder()
                .status("FORBIDDEN")
                .reason("For the requested operation the conditions are not met.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /*
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> notFoundException(NotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

     */

    /*
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(ConflictException e) {
        return new ErrorResponse(e.getMessage());
    }

     */
}
