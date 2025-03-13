package org.vnsemkin.t1openschool.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.vnsemkin.t1openschool.constant.TaskStatus;
import org.vnsemkin.t1openschool.dto.TaskDTO;
import org.vnsemkin.t1openschool.entity.Task;
import org.vnsemkin.t1openschool.exception.TaskNotFoundException;
import org.vnsemkin.t1openschool.service.task.TaskService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private TaskService taskService;

  private Task task;
  private TaskDTO taskDTO;

  @BeforeEach
  void setUp() {
    // Подготовка тестовых данных
    task = new Task();
    task.setId(1L);
    task.setTitle("Test Task");
    task.setDescription("Test Description");
    task.setUserId(1L);
    task.setStatus(TaskStatus.NEW);

    taskDTO =
            TaskDTO.builder()
                    .id(1L)
                    .title("Test Task")
                    .description("Test Description")
                    .userId(1L)
                    .status("NEW")
                    .build();
  }

  @Test
  @DisplayName("Создание задачи должно возвращать созданную задачу со статусом 201")
  void createTask_ShouldReturnCreatedTask() throws Exception {
    // Arrange - настраиваем поведение мока
    when(taskService.createTask(any(Task.class))).thenReturn(task);

    // Act & Assert - выполняем запрос и проверяем результат
    mockMvc
            .perform(
                    post("/api/v1/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(taskDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.title", is("Test Task")))
            .andExpect(jsonPath("$.description", is("Test Description")))
            .andExpect(jsonPath("$.userId", is(1)))
            .andExpect(jsonPath("$.status", is("NEW")));

    verify(taskService, times(1)).createTask(any(Task.class));
  }

  @Test
  @DisplayName("Получение задачи по ID должно возвращать задачу, если она существует")
  void getTask_ShouldReturnTask_WhenTaskExists() throws Exception {
    // Arrange - настраиваем поведение мока
    when(taskService.getTaskById(1L)).thenReturn(task);

    // Act & Assert - выполняем запрос и проверяем результат
    mockMvc
            .perform(get("/api/v1/tasks/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.title", is("Test Task")))
            .andExpect(jsonPath("$.description", is("Test Description")))
            .andExpect(jsonPath("$.userId", is(1)))
            .andExpect(jsonPath("$.status", is("NEW")));

    verify(taskService, times(1)).getTaskById(1L);
  }

  @Test
  @DisplayName("Получение задачи по несуществующему ID должно возвращать статус 404")
  void getTask_ShouldReturnNotFound_WhenTaskDoesNotExist() throws Exception {
    // Arrange - настраиваем поведение мока
    when(taskService.getTaskById(999L))
            .thenThrow(new TaskNotFoundException("Task с id 999 не найден."));

    // Act & Assert - выполняем запрос и проверяем результат
    mockMvc
            .perform(get("/api/v1/tasks/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", containsString("Task с id 999 не найден.")))
            .andExpect(jsonPath("$.status", is(404)));

    verify(taskService, times(1)).getTaskById(999L);
  }

  @Test
  @DisplayName("Обновление задачи должно возвращать обновленную задачу, если она существует")
  void updateTask_ShouldReturnUpdatedTask_WhenTaskExists() throws Exception {
    // Arrange - подготавливаем данные и настраиваем поведение мока
    Task updatedTask = new Task();
    updatedTask.setId(1L);
    updatedTask.setTitle("Updated Task");
    updatedTask.setDescription("Updated Description");
    updatedTask.setUserId(1L);
    updatedTask.setStatus(TaskStatus.IN_PROGRESS);

    TaskDTO updatedTaskDTO =
            TaskDTO.builder()
                    .id(1L)
                    .title("Updated Task")
                    .description("Updated Description")
                    .userId(1L)
                    .status("IN_PROGRESS")
                    .build();

    when(taskService.updateTask(eq(1L), any(Task.class)))
            .thenReturn(updatedTask);

    // Act & Assert - выполняем запрос и проверяем результат
    mockMvc
            .perform(
                    put("/api/v1/tasks/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedTaskDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.title", is("Updated Task")))
            .andExpect(jsonPath("$.description", is("Updated Description")))
            .andExpect(jsonPath("$.userId", is(1)))
            .andExpect(jsonPath("$.status", is("IN_PROGRESS")));

    verify(taskService, times(1)).updateTask(eq(1L), any(Task.class));
  }

  @Test
  @DisplayName("Обновление несуществующей задачи должно возвращать статус 404")
  void updateTask_ShouldReturnNotFound_WhenTaskDoesNotExist() throws Exception {
    // Arrange - настраиваем поведение мока
    when(taskService.updateTask(eq(999L), any(Task.class)))
            .thenThrow(new TaskNotFoundException("Task с id 999 не найден."));

    // Act & Assert - выполняем запрос и проверяем результат
    mockMvc
            .perform(
                    put("/api/v1/tasks/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(taskDTO)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", containsString("Task с id 999 не найден.")))
            .andExpect(jsonPath("$.status", is(404)));

    verify(taskService, times(1))
            .updateTask(eq(999L), any(Task.class));
  }

  @Test
  @DisplayName("Удаление задачи должно возвращать статус 204")
  void deleteTask_ShouldReturnNoContent() throws Exception {
    // Arrange - настраиваем поведение мока
    doNothing().when(taskService).deleteTask(1L);

    // Act & Assert - выполняем запрос и проверяем результат
    mockMvc.perform(delete("/api/v1/tasks/1"))
            .andExpect(status().isNoContent());

    verify(taskService, times(1)).deleteTask(1L);
  }

  @Test
  @DisplayName("Получение списка задач должно возвращать список задач")
  void getAllTasks_ShouldReturnTaskList() throws Exception {
    // Arrange - подготавливаем данные и настраиваем поведение мока
    Task task2 = new Task();
    task2.setId(2L);
    task2.setTitle("Test Task 2");
    task2.setDescription("Test Description 2");
    task2.setUserId(2L);
    task2.setStatus(TaskStatus.IN_PROGRESS);

    List<Task> tasks = Arrays.asList(task, task2);

    when(taskService.getAllTasks(any(Pageable.class))).thenReturn(tasks);

    // Act & Assert - выполняем запрос и проверяем результат
    mockMvc
            .perform(get("/api/v1/tasks?page=0&limit=10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].title", is("Test Task")))
            .andExpect(jsonPath("$[1].id", is(2)))
            .andExpect(jsonPath("$[1].title", is("Test Task 2")));

    // Проверяем, что сервис был вызван с правильными параметрами пагинации
    verify(taskService, times(1)).getAllTasks(eq(PageRequest.of(0, 10)));
  }

  @Test
  @DisplayName("Получение списка задач с пользовательскими параметрами пагинации")
  void getAllTasks_WithCustomPagination() throws Exception {
    // Arrange - настраиваем поведение мока
    when(taskService.getAllTasks(any(Pageable.class))).thenReturn(Arrays.asList(task));

    // Act & Assert - выполняем запрос с пользовательской пагинацией
    mockMvc
            .perform(get("/api/v1/tasks?page=2&limit=5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(1)));

    // Проверяем, что сервис был вызван с правильными параметрами пагинации
    verify(taskService).getAllTasks(eq(PageRequest.of(2, 5)));
  }
}