package org.vnsemkin.t1openschool.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.vnsemkin.t1openschool.model.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final String TASK_NOT_FOUND = "Task с id {} не найден.";
  private static final String UNHANDLED_EXCEPTION = "Unhandled exception: {}";
  private static final String INVALID_TASK_STATUS = "Неверное значение статуса: {}";

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, Object> response = new HashMap<>();
    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult()
        .getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

    response.put("errors", errors);
    response.put("status", HttpStatus.BAD_REQUEST.value());
    response.put("timestamp", LocalDateTime.now());

    log.error("Validation error: {}", errors);
    return response;
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleGeneralException(Exception ex) {
    log.error(UNHANDLED_EXCEPTION, ex.getMessage());
    return new ErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
  }

  @ExceptionHandler(TaskNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleTaskNotFoundException(TaskNotFoundException ex) {
    log.error(TASK_NOT_FOUND, ex.getMessage());
    return new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value());
  }

  @ExceptionHandler(InvalidTaskStatusException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleInvalidTaskStatusException(InvalidTaskStatusException ex) {
    log.error(INVALID_TASK_STATUS, ex.getMessage());
    return new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
  }
}
