package org.vnsemkin.t1openschool.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.vnsemkin.t1openschool.constant.TaskStatus;
import org.vnsemkin.t1openschool.exception.InvalidTaskStatusException;

@Builder
public record TaskDTO(
    Long id,
    String status,
    @NotBlank(message = "Title не должен быть пустым") String title,
    @NotBlank(message = "Description не должен быть пустым") String description,
    @NotNull(message = "UserId обязателен") Long userId) {

  public TaskDTO {
    if (status == null || status.isBlank()) {
      status = "NEW";
    } else {
      try {
        // Проверяем, что переданное значение соответствует одному из значений TaskStatus
        TaskStatus.valueOf(status);
      } catch (IllegalArgumentException ex) {
        throw new InvalidTaskStatusException("Неверное значение статуса: " + status);
      }
    }
  }
}