package org.vnsemkin.t1openschool.dto;

import lombok.Builder;

@Builder
public record TestTaskDTO(Long id, String status, String title, String description, Long userId) {}
