package faang.school.postservice.exception.handler;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.FeignClientException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.exception.PostException;
import faang.school.postservice.exception.UnauthorizedException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException e) {
        log.error("DataValidationException", e);
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .error(e.getClass().getName())
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("EntityNotFoundException", e);
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .error(e.getClass().getName())
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleUnauthorizedException(UnauthorizedException e) {
        log.error("UnauthorizedException", e);
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .error(e.getClass().getName())
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenException(ForbiddenException e) {
        log.error("ForbiddenException", e);
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .error(e.getClass().getName())
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(FeignClientException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ErrorResponse handleFeignClientException(FeignClientException e) {
        log.error("FeignClientException", e);
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .error(e.getClass().getName())
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(PostException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlePostException(PostException e) {
        log.error("PostException", e);
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .error(e.getClass().getName())
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e) {
        log.error("Exception", e);
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .error(e.getClass().getName())
                .message(e.getMessage())
                .build();
    }
}
