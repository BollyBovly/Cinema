package ru.stepan.cinema.exception;

import org.springframework.http.HttpStatus;
import java.time.Instant;

public record ApiError(Instant timestamp, int status, String error, String message) {

    public static ApiError create(HttpStatus status, String message) {
        return new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message);
    }
}
