package org.vnsemkin.t1openschool.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.vnsemkin.t1openschool.T1OpenSchoolApplicationTests;
import org.vnsemkin.t1openschool.dto.TaskDTO;
import org.vnsemkin.t1openschool.model.TaskStatusMessage;
import org.vnsemkin.t1openschool.repository.TaskRepository;

@AutoConfigureMockMvc
class TaskControllerIntegrationTest extends T1OpenSchoolApplicationTests {

  @Autowired MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private TaskRepository taskRepository;

  @MockitoBean private NewTopic taskStatusTopic;

  @MockitoBean private KafkaTemplate<String, TaskStatusMessage> taskStatusKafkaTemplate;

  private TaskDTO taskDTO;

  @BeforeEach
  void setUp() {
    // Очищаем базу данных перед каждым тестом
    taskRepository.deleteAll();

    // Готовим тестовые данные
    taskDTO =
            TaskDTO.builder()
                    .title("Integration Test Task")
                    .description("Integration Test Description")
                    .userId(1L)
                    .status("NEW")
                    .build();
  }

  @Test
  @DisplayName("Создание задачи должно сохранять задачу в БД и возвращать её со статусом 201")
  void createTask_ShouldCreateAndReturnTask() throws Exception {
    // Act & Assert - выполняем запрос и проверяем результат
    mockMvc
            .perform(
                    post("/api/v1/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(taskDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.title", is("Integration Test Task")))
            .andExpect(jsonPath("$.description", is("Integration Test Description")))
            .andExpect(jsonPath("$.userId", is(1)))
            .andExpect(jsonPath("$.status", is("NEW")));

    // Дополнительно проверяем, что задача сохранена в БД
    assertEquals(1, taskRepository.count(), "Количество задач в БД должно быть 1");
  }

  @Test
  @DisplayName("Получение задачи по ID должно возвращать задачу, если она существует")
  void getTask_ShouldReturnTask_WhenTaskExists() throws Exception {
    // Arrange - создаём задачу
    MvcResult createResult =
            mockMvc
                    .perform(
                            post("/api/v1/tasks")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(taskDTO)))
                    .andExpect(status().isCreated())
                    .andReturn();

    TaskDTO createdTask = objectMapper.readValue(
            createResult.getResponse().getContentAsString(),
            TaskDTO.class
    );

    // Act & Assert - получаем созданную задачу
    mockMvc
            .perform(get("/api/v1/tasks/" + createdTask.id()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(createdTask.id().intValue())))
            .andExpect(jsonPath("$.title", is("Integration Test Task")))
            .andExpect(jsonPath("$.description", is("Integration Test Description")))
            .andExpect(jsonPath("$.userId", is(1)))
            .andExpect(jsonPath("$.status", is("NEW")));
  }

  @Test
  @DisplayName("Получение несуществующей задачи должно возвращать статус 404")
  void getTask_ShouldReturnNotFound_WhenTaskDoesNotExist() throws Exception {
    // Act & Assert - пытаемся получить несуществующую задачу
    mockMvc
            .perform(get("/api/v1/tasks/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", containsString("Task с id 999 не найден.")))
            .andExpect(jsonPath("$.status", is(404)));
  }

  @Test
  @DisplayName("Обновление задачи должно изменять задачу в БД и возвращать обновленные данные")
  void updateTask_ShouldUpdateAndReturnTask_WhenTaskExists() throws Exception {
    // Arrange - создаём задачу
    MvcResult createResult =
            mockMvc
                    .perform(
                            post("/api/v1/tasks")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(taskDTO)))
                    .andExpect(status().isCreated())
                    .andReturn();

    TaskDTO createdTask = objectMapper.readValue(
            createResult.getResponse().getContentAsString(),
            TaskDTO.class
    );

    // Создаём обновленную задачу
    TaskDTO updatedTaskDTO =
            TaskDTO.builder()
                    .title("Updated Task")
                    .description("Updated Description")
                    .userId(2L)
                    .status("IN_PROGRESS")
                    .build();

    // Act & Assert - обновляем созданную задачу
    mockMvc
            .perform(
                    put("/api/v1/tasks/" + createdTask.id())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedTaskDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(createdTask.id().intValue())))
            .andExpect(jsonPath("$.title", is("Updated Task")))
            .andExpect(jsonPath("$.description", is("Updated Description")))
            .andExpect(jsonPath("$.userId", is(2)))
            .andExpect(jsonPath("$.status", is("IN_PROGRESS")));

    // Дополнительно проверяем, что задача обновлена в БД
    assertEquals(1, taskRepository.count(), "Количество задач в БД должно оставаться 1");
    assertTrue(taskRepository.findById(createdTask.id()).isPresent(), "Задача должна существовать в БД");
    assertEquals("Updated Task", taskRepository.findById(createdTask.id()).get().getTitle(),
            "Название задачи должно быть обновлено в БД");
  }

  @Test
  @DisplayName("Обновление несуществующей задачи должно возвращать статус 404")
  void updateTask_ShouldReturnNotFound_WhenTaskDoesNotExist() throws Exception {
    // Act & Assert - пытаемся обновить несуществующую задачу
    mockMvc
            .perform(
                    put("/api/v1/tasks/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(taskDTO)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", containsString("Task с id 999 не найден.")))
            .andExpect(jsonPath("$.status", is(404)));
  }

  @Test
  @DisplayName("Удаление задачи должно удалять задачу из БД")
  void deleteTask_ShouldDeleteTask() throws Exception {
    // Arrange - создаём задачу
    MvcResult createResult =
            mockMvc
                    .perform(
                            post("/api/v1/tasks")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(taskDTO)))
                    .andExpect(status().isCreated())
                    .andReturn();

    TaskDTO createdTask = objectMapper.readValue(
            createResult.getResponse().getContentAsString(),
            TaskDTO.class
    );

    // Проверяем, что задача создана в БД
    assertEquals(1, taskRepository.count(), "Задача должна быть создана в БД");

    // Act - удаляем созданную задачу
    mockMvc.perform(delete("/api/v1/tasks/" + createdTask.id()))
            .andExpect(status().isNoContent());

    // Assert - задача должна быть удалена из БД
    assertEquals(0, taskRepository.count(), "Задача должна быть удалена из БД");
    assertTrue(taskRepository.findById(createdTask.id()).isEmpty(),
            "Задача не должна существовать в БД после удаления");
  }

  @Test
  @DisplayName("Получение списка задач должно возвращать все задачи с учетом пагинации")
  void getAllTasks_ShouldReturnTasks() throws Exception {
    // Arrange - создаём две задачи
    mockMvc
            .perform(
                    post("/api/v1/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(taskDTO)))
            .andExpect(status().isCreated());

    TaskDTO task2DTO =
            TaskDTO.builder()
                    .title("Second Task")
                    .description("Second Description")
                    .userId(2L)
                    .status("IN_PROGRESS")
                    .build();

    mockMvc
            .perform(
                    post("/api/v1/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(task2DTO)))
            .andExpect(status().isCreated());

    // Act & Assert - получаем список всех задач
    mockMvc
            .perform(get("/api/v1/tasks?page=0&limit=10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].title", is("Integration Test Task")))
            .andExpect(jsonPath("$[1].title", is("Second Task")));

    // Проверяем пагинацию - запрашиваем первую страницу с лимитом 1
    mockMvc
            .perform(get("/api/v1/tasks?page=0&limit=1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].title", is("Integration Test Task")));

    // Проверяем пагинацию - запрашиваем вторую страницу с лимитом 1
    mockMvc
            .perform(get("/api/v1/tasks?page=1&limit=1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].title", is("Second Task")));
  }
}