package org.vnsemkin.t1openschool.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final String ERROR = "error";
  private static final String TASK_NOT_FOUND = "Task с id %d не найден.";
  private static final String UNHANDLED_EXCEPTION = "Unhandled exception: {}";

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
    log.error("Validation error: {}", errors);
    return errors;
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String handleGeneralException(Exception ex) {
    return handleException(ex, UNHANDLED_EXCEPTION);
  }

  @ExceptionHandler(TaskNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public String handleTaskNotFoundException(TaskNotFoundException ex) {
    return handleException(ex, TASK_NOT_FOUND);
  }

  private String handleException(@NonNull Exception ex, @NonNull String message) {
    log.error(message, ex.getMessage());
    return ERROR + ": " + ex.getMessage();
  }
}
