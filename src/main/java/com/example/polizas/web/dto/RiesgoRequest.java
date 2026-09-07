package com.example.polizas.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record RiesgoRequest(
        @NotBlank(message = "arrendatario es obligatorio") String arrendatario,
        @NotBlank(message = "direccion es obligatoria") String direccion,
        @NotNull(message = "canonMensual es obligatorio")
        @Positive(message = "canonMensual debe ser mayor a 0") BigDecimal canonMensual
) {
}
