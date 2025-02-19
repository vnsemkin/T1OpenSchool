package org.vnsemkin.t1openschool.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TaskDTO(
    @NotBlank(message = "Title не должен быть пустым") String title,
    @NotBlank(message = "Description не должен быть пустым") String description,
    @NotNull(message = "UserId обязателен") Long userId) {}
