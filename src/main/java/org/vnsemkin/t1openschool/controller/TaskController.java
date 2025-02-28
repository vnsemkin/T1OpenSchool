package org.vnsemkin.t1openschool.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.vnsemkin.t1openschool.dto.TaskDTO;
import org.vnsemkin.t1openschool.mapper.TaskMapper;
import org.vnsemkin.t1openschool.service.task.TaskService;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

  private final TaskService taskService;

  // POST /tasks — создание новой задачи.
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TaskDTO createTask(@Valid @RequestBody TaskDTO taskDTO) {
    return TaskMapper.toDTO(taskService.createTask(TaskMapper.toEntity(taskDTO)));
  }

  // GET /tasks/{id} — получение задачи по ID.
  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public TaskDTO getTask(@PathVariable Long id) {
    return TaskMapper.toDTO(taskService.getTaskById(id));
  }

  // PUT /tasks/{id} — обновление задачи.
  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public TaskDTO updateTask(@PathVariable Long id, @Valid @RequestBody TaskDTO taskDTO) {
    return TaskMapper.toDTO(taskService.updateTask(id, TaskMapper.toEntity(taskDTO)));
  }

  // DELETE /tasks/{id} — удаление задачи.
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteTask(@PathVariable Long id) {
    taskService.deleteTask(id);
  }

  // GET /tasks — получение списка всех задач с пагинацией (offset и limit)
  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<TaskDTO> getAllTasks(
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "limit", defaultValue = "10") int limit) {

    return taskService.getAllTasks(PageRequest.of(page, limit)).stream()
        .map(TaskMapper::toDTO)
        .collect(Collectors.toList());
  }
}