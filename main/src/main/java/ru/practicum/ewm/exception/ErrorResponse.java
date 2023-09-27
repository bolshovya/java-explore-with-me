package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;

public class ErrorResponse {

    private String error;
    private HttpStatus httpStatus;


    public ErrorResponse(String error) {
        this.error = error;
    }

    public ErrorResponse(String error, HttpStatus httpStatus) {
        this.error = error;
        this.httpStatus = httpStatus;
    }
}