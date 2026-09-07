package com.example.polizas.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CoreEventRequest(
        @NotBlank(message = "evento es obligatorio") String evento,
        @NotNull(message = "polizaId es obligatorio") Long polizaId
) {
}
