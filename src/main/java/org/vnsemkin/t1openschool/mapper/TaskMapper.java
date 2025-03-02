package org.vnsemkin.t1openschool.mapper;

import org.springframework.lang.NonNull;
import org.vnsemkin.t1openschool.constant.TaskStatus;
import org.vnsemkin.t1openschool.dto.TaskDTO;
import org.vnsemkin.t1openschool.entity.Task;

public class TaskMapper {
  public static TaskDTO toDTO(@NonNull Task task) {
    return TaskDTO.builder()
        .id(task.getId())
        .title(task.getTitle())
        .userId(task.getUserId())
        .status(task.getStatus().name())
        .description(task.getDescription())
        .build();
  }

  public static Task toEntity(@NonNull TaskDTO taskDTO) {
    Task task = new Task();
    task.setId(taskDTO.id());
    task.setTitle(taskDTO.title());
    task.setUserId(taskDTO.userId());
    task.setDescription(taskDTO.description());
    task.setStatus(TaskStatus.valueOf(taskDTO.status()));
    return task;
  }
}