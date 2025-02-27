package org.vnsemkin.t1openschool.constant;

public enum TaskStatus {
  NEW("New"),
  IN_PROGRESS("In progress"),
  DONE("Done");

  private final String value;

  TaskStatus(String value) {
    this.value = value;
  }
}