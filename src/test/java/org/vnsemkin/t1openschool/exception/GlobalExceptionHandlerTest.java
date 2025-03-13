package org.vnsemkin.t1openschool.exception;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.vnsemkin.t1openschool.controller.TaskController;
import org.vnsemkin.t1openschool.dto.TestTaskDTO;
import org.vnsemkin.t1openschool.service.task.TaskService;

@WebMvcTest(TaskController.class)
class GlobalExceptionHandlerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private TaskService taskService;

  @Test
  @DisplayName("Обработчик должен возвращать статус 404 при ненайденной задаче")
  void handleTaskNotFoundException_ShouldReturnNotFoundStatus() throws Exception {
    // Arrange - настраиваем мок для выброса исключения при запросе задачи
    when(taskService.getTaskById(anyLong())).thenThrow(new TaskNotFoundException("Task с id 1 не найден."));

    // Act & Assert - выполняем запрос и проверяем результат
    mockMvc.perform(get("/api/v1/tasks/1"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", is("Task с id 1 не найден.")))
            .andExpect(jsonPath("$.status", is(404)));
  }

  @Test
  @DisplayName("Обработчик должен возвращать статус 400 при неверном статусе задачи")
  void handleInvalidTaskStatusException_ShouldReturnBadRequestStatus() throws Exception {
    // Arrange - подготавливаем данные с недопустимым статусом
    TestTaskDTO invalidTaskDTO = TestTaskDTO.builder()
            .title("Test Task")
            .description("Test Description")
            .userId(1L)
            .status("INVALID_STATUS")
            .build();

    // Act & Assert - выполняем запрос и проверяем результат
    mockMvc.perform(post("/api/v1/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidTaskDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("Неверное значение статуса")))
            .andExpect(jsonPath("$.status", is(400)));
  }

  @Test
  @DisplayName("Обработчик должен возвращать статус 400 с перечнем ошибок валидации")
  void handleValidationExceptions_ShouldReturnBadRequestStatus() throws Exception {
    // Arrange - создаём DTO с отсутствующими обязательными полями
    TestTaskDTO invalidTaskDTO = TestTaskDTO.builder()
            .title("")  // пустое название - не должно проходить валидацию
            .description("Test Description")
            .userId(null)  // null userId - не должно проходить валидацию
            .status("NEW")
            .build();

    // Act & Assert - выполняем запрос и проверяем результат
    mockMvc.perform(post("/api/v1/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidTaskDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors.title", containsString("не должен быть пустым")))
            .andExpect(jsonPath("$.errors.userId", containsString("обязателен")))
            .andExpect(jsonPath("$.status", is(400)));
  }

  @Test
  @DisplayName("Обработчик должен возвращать статус 500 при необработанных исключениях")
  void handleGeneralException_ShouldReturnInternalServerErrorStatus() throws Exception {
    // Arrange - настраиваем мок для выброса исключения при запросе задачи
    when(taskService.getTaskById(anyLong())).thenThrow(new RuntimeException("Unexpected error"));

    // Act & Assert - выполняем запрос и проверяем результат
    mockMvc.perform(get("/api/v1/tasks/1"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message", is("Unexpected error")))
            .andExpect(jsonPath("$.status", is(500)));
  }

  @Test
  @DisplayName("Обработчик должен возвращать статус 400 при ошибке разбора JSON")
  void handleHttpMessageConversionException_ShouldReturnBadRequestStatus() throws Exception {
    // Arrange - настраиваем мок для выброса исключения при разборе JSON
    when(taskService.getTaskById(anyLong())).thenThrow(
            new HttpMessageConversionException("Ошибка при разборе JSON"));

    // Act & Assert - выполняем запрос и проверяем результат
    mockMvc.perform(get("/api/v1/tasks/1"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("Ошибка формата данных")))
            .andExpect(jsonPath("$.status", is(400)));
  }
}