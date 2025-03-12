package org.vnsemkin.t1openschool.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.vnsemkin.t1openschool.constant.TaskStatus;
import org.vnsemkin.t1openschool.exception.InvalidTaskStatusException;

import static org.junit.jupiter.api.Assertions.*;

public class TaskDTOTest {

  @Test
  @DisplayName("Конструктор должен устанавливать статус NEW при null-значении статуса")
  void constructor_ShouldSetDefaultStatusToNEW_WhenStatusIsNull() {
    // Arrange & Act - создаем DTO с null-статусом
    TaskDTO taskDTO = TaskDTO.builder()
            .id(1L)
            .title("Test Task")
            .description("Test Description")
            .userId(1L)
            .status(null)
            .build();

    // Assert - проверяем, что статус установлен в NEW
    assertEquals("NEW", taskDTO.status());
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "  "})
  @DisplayName("Конструктор должен устанавливать статус NEW при пустом или пробельном значении статуса")
  void constructor_ShouldSetDefaultStatusToNEW_WhenStatusIsBlankOrEmpty(String blankStatus) {
    // Arrange & Act - создаем DTO с пустым или пробельным статусом
    TaskDTO taskDTO = TaskDTO.builder()
            .id(1L)
            .title("Test Task")
            .description("Test Description")
            .userId(1L)
            .status(blankStatus)
            .build();

    // Assert - проверяем, что статус установлен в NEW
    assertEquals("NEW", taskDTO.status());
  }

  @ParameterizedTest
  @EnumSource(TaskStatus.class)
  @DisplayName("Конструктор должен принимать все валидные значения статуса")
  void constructor_ShouldAcceptValidStatus(TaskStatus validStatus) {
    // Arrange & Act - создаем DTO с валидным статусом
    TaskDTO taskDTO = TaskDTO.builder()
            .id(1L)
            .title("Test Task")
            .description("Test Description")
            .userId(1L)
            .status(validStatus.name())
            .build();

    // Assert - проверяем, что статус установлен корректно
    assertEquals(validStatus.name(), taskDTO.status());
  }

  @Test
  @DisplayName("Конструктор должен выбрасывать исключение при невалидном статусе")
  void constructor_ShouldThrowException_WhenStatusIsInvalid() {
    // Arrange, Act & Assert - проверяем, что при невалидном статусе выбрасывается исключение
    InvalidTaskStatusException exception = assertThrows(InvalidTaskStatusException.class, () -> {
      TaskDTO.builder()
              .id(1L)
              .title("Test Task")
              .description("Test Description")
              .userId(1L)
              .status("INVALID_STATUS")
              .build();
    });

    // Проверяем сообщение об ошибке
    assertTrue(exception.getMessage().contains("Неверное значение статуса"));
  }

  @Test
  @DisplayName("Конструктор должен корректно работать с минимально необходимыми данными")
  void constructor_ShouldWorkWithMinimalData() {
    // Arrange & Act - создаем DTO только с обязательными полями
    TaskDTO taskDTO = TaskDTO.builder()
            .title("Minimal Task")
            .userId(1L)
            .build();

    // Assert - проверяем, что DTO создан корректно
    assertNotNull(taskDTO);
    assertEquals("Minimal Task", taskDTO.title());
    assertEquals(1L, taskDTO.userId());
    assertEquals("NEW", taskDTO.status()); // Проверяем значение по умолчанию
    assertNull(taskDTO.id()); // ID не был установлен
    assertNull(taskDTO.description()); // Описание не было установлено
  }
}