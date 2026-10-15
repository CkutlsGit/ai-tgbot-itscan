package ru.itscan.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import ru.itscan.dto.ExceptionDto;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ExceptionDto> httpClientErrorExceptionHandler(HttpClientErrorException e) {
        log.error("Ошибка на стороне клиента", e);
        return ResponseEntity.status(e.getStatusCode()).body(new ExceptionDto("Ошибка на стороне клиента"));
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ExceptionDto> httpServerErrorExceptionHandler(HttpServerErrorException e) {
        log.error("Ошибка на стороне сервера", e);
        return ResponseEntity.status(e.getStatusCode()).body(new ExceptionDto("Ошибка на стороне сервера."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> exceptionHandler(Exception e) {
        log.error("Необрабатываемая ошибка", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExceptionDto("Необрабатываемая ошибка"));
    }
}
