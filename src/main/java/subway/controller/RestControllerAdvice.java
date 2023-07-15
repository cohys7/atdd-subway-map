package subway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import subway.exception.AddSectionException;

@ControllerAdvice
public class RestControllerAdvice {
    @ExceptionHandler(value = {AddSectionException.class})
    public ResponseEntity<String> AddSectionExceptionHandler(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}