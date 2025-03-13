package org.vnsemkin.t1openschool.service.task.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.vnsemkin.t1openschool.constant.TaskStatus;
import org.vnsemkin.t1openschool.entity.Task;
import org.vnsemkin.t1openschool.exception.TaskNotFoundException;
import org.vnsemkin.t1openschool.model.TaskStatusMessage;
import org.vnsemkin.t1openschool.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

  @Mock private TaskRepository taskRepository;

  @Mock private NewTopic taskStatusTopic;

  @Mock private KafkaTemplate<String, TaskStatusMessage> taskStatusKafkaTemplate;

  @InjectMocks private TaskServiceImpl taskService;

  private Task task;
  private List<Task> taskList;

  @BeforeEach
  void setUp() {
    // Подготовка тестовых данных
    task = new Task();
    task.setId(1L);
    task.setTitle("Test Task");
    task.setDescription("Test Description");
    task.setUserId(1L);
    task.setStatus(TaskStatus.NEW);

    Task task2 = new Task();
    task2.setId(2L);
    task2.setTitle("Test Task 2");
    task2.setDescription("Test Description 2");
    task2.setUserId(1L);
    task2.setStatus(TaskStatus.IN_PROGRESS);

    taskList = List.of(task, task2);
  }

  @Test
  @DisplayName("Создание задачи должно возвращать созданную задачу")
  void createTask_ShouldReturnCreatedTask() {
    // Arrange - настраиваем поведение мок-объекта
    when(taskRepository.save(any(Task.class))).thenReturn(task);

    // Act - вызываем тестируемый метод
    Task savedTask = taskService.createTask(task);

    // Assert - проверяем результат
    assertNotNull(savedTask);
    assertEquals(task.getId(), savedTask.getId());
    assertEquals(task.getTitle(), savedTask.getTitle());
    verify(taskRepository, times(1)).save(task);
  }

  @Test
  @DisplayName("Получение задачи по ID должно возвращать задачу, если она существует")
  void getTaskById_ShouldReturnTask_WhenTaskExists() {
    // Arrange - настраиваем поведение мок-объекта
    when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

    // Act - вызываем тестируемый метод
    Task foundTask = taskService.getTaskById(1L);

    // Assert - проверяем результат
    assertNotNull(foundTask);
    assertEquals(1L, foundTask.getId());
    verify(taskRepository, times(1)).findById(1L);
  }

  @Test
  @DisplayName("Получение задачи по несуществующему ID должно вызывать исключение")
  void getTaskById_ShouldThrowException_WhenTaskDoesNotExist() {
    // Arrange - настраиваем поведение мок-объекта
    when(taskRepository.findById(999L)).thenReturn(Optional.empty());

    // Act & Assert - проверяем, что вызывается ожидаемое исключение
    TaskNotFoundException exception =
        assertThrows(
            TaskNotFoundException.class,
            () -> {
              taskService.getTaskById(999L);
            });

    // Проверяем содержимое сообщения об ошибке
    assertEquals("Task с id 999 не найден.", exception.getMessage());
    verify(taskRepository, times(1)).findById(999L);
  }

  @ParameterizedTest
  @EnumSource(TaskStatus.class)
  @DisplayName("Обновление задачи должно отправлять сообщение в Kafka при изменении статуса")
  void updateTask_ShouldSendMessageToKafka_WhenStatusChanged(TaskStatus newStatus) {
    // Пропускаем тест, если статус не меняется
    if (newStatus == TaskStatus.NEW) {
      return;
    }

    // Arrange - подготавливаем данные и настраиваем поведение мок-объектов
    Task updatedTask = new Task();
    updatedTask.setTitle("Updated Title");
    updatedTask.setDescription("Updated Description");
    updatedTask.setUserId(1L);
    updatedTask.setStatus(newStatus);

    when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
    when(taskRepository.save(any(Task.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
    when(taskStatusTopic.name()).thenReturn("task-status-topic");

    // Act - вызываем тестируемый метод
    Task result = taskService.updateTask(1L, updatedTask);

    // Assert - проверяем результат
    assertNotNull(result);
    assertEquals("Updated Title", result.getTitle());
    assertEquals("Updated Description", result.getDescription());
    assertEquals(newStatus, result.getStatus());

    // Проверяем отправку сообщения в Kafka
    ArgumentCaptor<TaskStatusMessage> messageCaptor =
        ArgumentCaptor.forClass(TaskStatusMessage.class);
    verify(taskStatusKafkaTemplate).send(eq("task-status-topic"), messageCaptor.capture());

    TaskStatusMessage capturedMessage = messageCaptor.getValue();
    assertEquals(1L, capturedMessage.id());
    assertEquals(newStatus.name(), capturedMessage.status());
  }

  @Test
  @DisplayName("Обновление задачи не должно отправлять сообщение в Kafka, если статус не изменился")
  void updateTask_ShouldNotSendKafkaMessage_WhenStatusNotChanged() {
    // Arrange - подготавливаем данные и настраиваем поведение мок-объектов
    Task sameStatusTask = new Task();
    sameStatusTask.setTitle("Updated Title");
    sameStatusTask.setDescription("Updated Description");
    sameStatusTask.setUserId(1L);
    sameStatusTask.setStatus(TaskStatus.NEW); // Тот же статус, что и у изначальной задачи

    when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
    when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Act - вызываем тестируемый метод
    Task result = taskService.updateTask(1L, sameStatusTask);

    // Assert - проверяем результат
    assertNotNull(result);
    assertEquals("Updated Title", result.getTitle());
    assertEquals(TaskStatus.NEW, result.getStatus());
    verify(taskRepository).findById(1L);
    verify(taskRepository).save(any(Task.class));
    // Проверяем, что сообщение в Kafka НЕ отправлялось, т.к. статус не изменился
    verify(taskStatusKafkaTemplate, never()).send(anyString(), any(TaskStatusMessage.class));
  }

  @Test
  @DisplayName("Обновление несуществующей задачи должно вызывать исключение")
  void updateTask_ShouldThrowException_WhenTaskDoesNotExist() {
    // Arrange - настраиваем поведение мок-объекта
    when(taskRepository.findById(999L)).thenReturn(Optional.empty());

    // Act & Assert - проверяем, что вызывается ожидаемое исключение
    TaskNotFoundException exception =
        assertThrows(
            TaskNotFoundException.class,
            () -> {
              taskService.updateTask(999L, task);
            });

    // Проверяем содержимое сообщения об ошибке
    assertEquals("Task с id 999 не найден.", exception.getMessage());
    verify(taskRepository).findById(999L);
    verify(taskRepository, never()).save(any(Task.class));
  }

  @Test
  @DisplayName("Удаление задачи должно вызывать соответствующий метод репозитория")
  void deleteTask_ShouldCallRepositoryDeleteById() {
    // Arrange - настраиваем поведение мок-объекта
    doNothing().when(taskRepository).deleteById(1L);

    // Act - вызываем тестируемый метод
    taskService.deleteTask(1L);

    // Assert - проверяем, что был вызван ожидаемый метод
    verify(taskRepository).deleteById(1L);
  }

  @Test
  @DisplayName("Получение всех задач должно возвращать список задач")
  void getAllTasks_ShouldReturnTaskList() {
    // Arrange - настраиваем поведение мок-объекта
    Pageable pageable = PageRequest.of(0, 10);
    Page<Task> page = new PageImpl<>(taskList);
    when(taskRepository.findAll(pageable)).thenReturn(page);

    // Act - вызываем тестируемый метод
    List<Task> result = taskService.getAllTasks(pageable);

    // Assert - проверяем результат
    assertNotNull(result);
    assertEquals(2, result.size());
    verify(taskRepository).findAll(pageable);
  }

  @Test
  @DisplayName("Получение всех задач должно возвращать пустой список, если задач нет")
  void getAllTasks_ShouldReturnEmptyList_WhenNoTasksExist() {
    // Arrange - настраиваем поведение мок-объекта
    Pageable pageable = PageRequest.of(0, 10);
    Page<Task> emptyPage = new PageImpl<>(Collections.emptyList());
    when(taskRepository.findAll(pageable)).thenReturn(emptyPage);

    // Act - вызываем тестируемый метод
    List<Task> result = taskService.getAllTasks(pageable);

    // Assert - проверяем результат
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(taskRepository).findAll(pageable);
  }

  @Test
  @DisplayName("Обновление задачи должно корректно обрабатывать ошибки отправки в Kafka")
  void updateTask_ShouldHandleKafkaException() {
    // Arrange - подготавливаем данные и настраиваем поведение мок-объектов
    Task updatedTask = new Task();
    updatedTask.setTitle("Updated Title");
    updatedTask.setDescription("Updated Description");
    updatedTask.setUserId(1L);
    updatedTask.setStatus(TaskStatus.IN_PROGRESS); // Изменение статуса

    when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
    when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(taskStatusTopic.name()).thenReturn("task-status-topic");
    doThrow(new RuntimeException("Kafka connection error"))
        .when(taskStatusKafkaTemplate)
        .send(anyString(), any(TaskStatusMessage.class));

    // Act - вызываем тестируемый метод
    Task result = taskService.updateTask(1L, updatedTask);

    // Assert - проверяем, что обновление задачи произошло успешно, несмотря на ошибку Kafka
    assertNotNull(result);
    assertEquals("Updated Title", result.getTitle());
    assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
    verify(taskRepository).findById(1L);
    verify(taskRepository).save(any(Task.class));
    verify(taskStatusKafkaTemplate).send(anyString(), any(TaskStatusMessage.class));
  }
}
