package faang.school.postservice.exeption;

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

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleFeignException(EntityNotFoundException e) {
        log.error(e.getMessage());
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                "Entity not found exception",
                e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleFeignException(DataValidationException e) {
        log.error(e.getMessage());
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                "Data validation exception",
                e.getMessage(),
                LocalDateTime.now());
    }
}
