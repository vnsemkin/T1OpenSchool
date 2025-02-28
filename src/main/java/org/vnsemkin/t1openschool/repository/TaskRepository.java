package org.vnsemkin.t1openschool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vnsemkin.t1openschool.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
}