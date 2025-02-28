package org.vnsemkin.t1openschool.entity;

import jakarta.persistence.*;
import lombok.*;
import org.vnsemkin.t1openschool.constant.TaskStatus;

@Getter
@Setter
@Entity
@Table(name = "task")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Task {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;

  private String description;

  @Column(name = "user_id")
  private Long userId;

  private TaskStatus status;
}