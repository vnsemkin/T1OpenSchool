package org.vnsemkin.t1openschool.service.task;

import org.vnsemkin.t1openschool.entity.Task;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface TaskService {
  Task createTask(Task task);

  Task getTaskById(Long id);

  Task updateTask(Long id, Task task);

  void deleteTask(Long id);

  List<Task> getAllTasks(Pageable pageable);
}