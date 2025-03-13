package org.vnsemkin.t1openschool.mapper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.vnsemkin.t1openschool.constant.TaskStatus;
import org.vnsemkin.t1openschool.dto.TaskDTO;
import org.vnsemkin.t1openschool.entity.Task;

public class TaskMapperTest {

  @Test
  void toDTO_ShouldMapTaskToTaskDTO() {
    // Arrange - подготавливаем тестовые данные
    Task task = getTask();

    // Act - вызываем тестируемый метод
    TaskDTO taskDTO = TaskMapper.toDTO(task);

    // Assert - проверяем результат
    assertMappingTaskToTaskDTO(taskDTO);
  }

  @Test
  void toEntity_ShouldMapTaskDTOToTask() {
    // Arrange - подготавливаем тестовые данные
    TaskDTO taskDTO = getTaskDTO();

    // Act - вызываем тестируемый метод
    Task task = TaskMapper.toEntity(taskDTO);

    // Assert - проверяем результат
    assertMappingTaskDTOToTask(task);
  }

  // Добавляем параметризованный тест для разных статусов задач
  @ParameterizedTest
  @EnumSource(TaskStatus.class)
  void toDTO_ShouldHandleDifferentStatuses(TaskStatus status) {
    // Arrange - подготавливаем задачу с различными статусами
    Task task = getTask();
    task.setStatus(status);

    // Act - вызываем тестируемый метод
    TaskDTO taskDTO = TaskMapper.toDTO(task);

    // Assert - проверяем, что статус маппируется корректно
    assertEquals(status.name(), taskDTO.status());
  }

  // Добавляем тест для проверки обработки null-значений
  @Test
  void toEntity_ShouldHandleNullDescription() {
    // Arrange - подготавливаем DTO с null-описанием
    TaskDTO taskDTO = TaskDTO.builder()
            .id(1L)
            .title("Test Task")
            .description(null) // Проверяем null-описание
            .userId(1L)
            .status("NEW")
            .build();

    // Act - вызываем тестируемый метод
    Task task = TaskMapper.toEntity(taskDTO);

    // Assert - проверяем, что null-описание корректно маппируется
    assertNull(task.getDescription());
    assertEquals(1L, task.getId());
    assertEquals("Test Task", task.getTitle());
    assertEquals(1L, task.getUserId());
    assertEquals(TaskStatus.NEW, task.getStatus());
  }

  private Task getTask() {
    Task task = new Task();
    task.setId(1L);
    task.setTitle("Test Task");
    task.setDescription("Test Description");
    task.setUserId(1L);
    task.setStatus(TaskStatus.NEW);
    return task;
  }

  private TaskDTO getTaskDTO() {
    return TaskDTO.builder()
            .id(1L)
            .title("Test Task")
            .description("Test Description")
            .userId(1L)
            .status("NEW")
            .build();
  }

  private void assertMappingTaskToTaskDTO(TaskDTO taskDTO) {
    assertNotNull(taskDTO);
    assertEquals(1L, taskDTO.id());
    assertEquals("Test Task", taskDTO.title());
    assertEquals("Test Description", taskDTO.description());
    assertEquals(1L, taskDTO.userId());
    assertEquals("NEW", taskDTO.status());
  }

  private void assertMappingTaskDTOToTask(Task task) {
    assertNotNull(task);
    assertEquals(1L, task.getId());
    assertEquals("Test Task", task.getTitle());
    assertEquals("Test Description", task.getDescription());
    assertEquals(1L, task.getUserId());
    assertEquals(TaskStatus.NEW, task.getStatus());
  }
}