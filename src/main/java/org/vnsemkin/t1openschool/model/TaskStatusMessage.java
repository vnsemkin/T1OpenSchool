package org.vnsemkin.t1openschool.model;

import lombok.Builder;
import org.springframework.lang.NonNull;

@Builder
public record TaskStatusMessage(@NonNull Long id, @NonNull String status) {}
