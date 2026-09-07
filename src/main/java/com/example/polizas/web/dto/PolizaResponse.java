package com.example.polizas.web.dto;

import com.example.polizas.domain.EstadoPoliza;
import com.example.polizas.domain.Poliza;
import com.example.polizas.domain.TipoPoliza;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PolizaResponse(
        Long id,
        String numero,
        TipoPoliza tipo,
        EstadoPoliza estado,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        BigDecimal canonMensual,
        BigDecimal prima,
        BigDecimal porcentajeIpc
) {
    public static PolizaResponse from(Poliza p) {
        return new PolizaResponse(p.getId(), p.getNumero(), p.getTipo(), p.getEstado(),
                p.getFechaInicio(), p.getFechaFin(), p.getCanonMensual(), p.getPrima(), p.getPorcentajeIpc());
    }
}
