package com.example.polizas.web.dto;

import com.example.polizas.domain.EstadoRiesgo;
import com.example.polizas.domain.Riesgo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RiesgoResponse(
        Long id,
        Long polizaId,
        String arrendatario,
        String direccion,
        BigDecimal canonMensual,
        EstadoRiesgo estado,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaCancelacion
) {
    public static RiesgoResponse from(Riesgo r) {
        return new RiesgoResponse(r.getId(), r.getPoliza().getId(), r.getArrendatario(), r.getDireccion(),
                r.getCanonMensual(), r.getEstado(), r.getFechaCreacion(), r.getFechaCancelacion());
    }
}
