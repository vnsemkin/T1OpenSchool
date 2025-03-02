package org.vnsemkin.t1openschool.model;

import org.springframework.lang.NonNull;

public record ErrorResponse(@NonNull String message, int status) {}
