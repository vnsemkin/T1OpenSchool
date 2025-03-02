package org.vnsemkin.t1openschool.exception;

public class InvalidTaskStatusException extends RuntimeException {
  public InvalidTaskStatusException(String message) {
    super(message);
  }
}