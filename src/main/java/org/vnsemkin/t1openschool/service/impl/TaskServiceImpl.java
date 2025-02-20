package org.vnsemkin.t1openschool.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vnsemkin.t1openschool.entity.Task;
import org.vnsemkin.t1openschool.exception.TaskNotFoundException;
import org.vnsemkin.t1openschool.repository.TaskRepository;
import org.vnsemkin.t1openschool.service.TaskService;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

  private static final String TASK_NOT_FOUND = "Task с id %d не найден.";

  private final TaskRepository taskRepository;

  @Override
  public Task createTask(@NonNull Task task) {
    return taskRepository.save(task);
  }

  @Override
  public Task getTaskById(@NonNull Long id) {
    return taskRepository
        .findById(id)
        .orElseThrow(() -> new TaskNotFoundException(String.format(TASK_NOT_FOUND, id)));
  }

  @Override
  public Task updateTask(@NonNull Long id, @NonNull Task task) {
    return taskRepository
        .findById(id)
        .map(
            existingTask -> {
              existingTask.setTitle(task.getTitle());
              existingTask.setDescription(task.getDescription());
              existingTask.setUserId(task.getUserId());
              return taskRepository.save(existingTask);
            })
        .orElseThrow(() -> new TaskNotFoundException(String.format(TASK_NOT_FOUND, id)));
  }

  @Override
  public void deleteTask(Long id) {
    taskRepository.deleteById(id);
  }

  @Override
  public List<Task> getAllTasks(Pageable pageable) {
    return taskRepository.findAll(pageable).getContent();
  }
}
