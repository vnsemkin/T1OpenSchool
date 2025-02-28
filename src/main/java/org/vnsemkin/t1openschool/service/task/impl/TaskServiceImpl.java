package org.vnsemkin.t1openschool.service.task.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vnsemkin.t1openschool.constant.TaskStatus;
import org.vnsemkin.t1openschool.entity.Task;
import org.vnsemkin.t1openschool.exception.TaskNotFoundException;
import org.vnsemkin.t1openschool.model.TaskStatusMessage;
import org.vnsemkin.t1openschool.repository.TaskRepository;
import org.vnsemkin.t1openschool.service.task.TaskService;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

  private static final String TASK_NOT_FOUND = "Task с id %d не найден.";

  @Value("${spring.kafka.topic}")
  private String topic;

  private final TaskRepository taskRepository;
  private final KafkaTemplate<String, TaskStatusMessage> taskStatusKafkaTemplate;

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
              // Сохраняем старый статус до обновления
              TaskStatus oldStatus = existingTask.getStatus();
              // Обновляем поля
              existingTask.setStatus(task.getStatus());
              existingTask.setTitle(task.getTitle());
              existingTask.setDescription(task.getDescription());
              existingTask.setUserId(task.getUserId());
              Task savedTask = taskRepository.save(existingTask);
              log.info(
                  "Task updated with id {} and status {}",
                  savedTask.getId(),
                  savedTask.getStatus());
              if (!oldStatus.equals(savedTask.getStatus())) {
                sendMessageToKafka(savedTask);
              }
              return savedTask;
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

  private void sendMessageToKafka(@NonNull Task updatedTask) {
    // Формируем сообщение для Kafka
    TaskStatusMessage message =
        TaskStatusMessage.builder()
            .id(updatedTask.getId())
            .status(updatedTask.getStatus().name())
            .build();
    // Отправляем сообщение в Kafka
    try {
      taskStatusKafkaTemplate.send(topic, message);
      log.info(
          "Kafka message sent for task id {} with new status {}",
          updatedTask.getId(),
          updatedTask.getStatus().name());
    } catch (Exception e) {
      log.error("Error sending Kafka message: {}", e.getMessage());
    }
  }
}