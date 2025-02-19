package org.vnsemkin.t1openschool.mapper;

import org.springframework.lang.NonNull;
import org.vnsemkin.t1openschool.dto.TaskDTO;
import org.vnsemkin.t1openschool.entity.Task;

public class TaskMapper {
  public static TaskDTO toDTO(@NonNull Task task) {
    return TaskDTO.builder()
        .title(task.getTitle())
        .description(task.getDescription())
        .userId(task.getUserId())
        .build();
  }

  public static Task toEntity(@NonNull TaskDTO taskDTO) {
    Task task = new Task();
    task.setTitle(taskDTO.title());
    task.setDescription(taskDTO.description());
    task.setUserId(taskDTO.userId());
    return task;
  }
}
