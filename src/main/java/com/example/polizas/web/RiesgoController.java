package com.example.polizas.web;

import com.example.polizas.service.PolizaService;
import com.example.polizas.web.dto.RiesgoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/riesgos")
@Tag(name = "Riesgos", description = "Gestion puntual de riesgos")
public class RiesgoController {

    private final PolizaService service;

    public RiesgoController(PolizaService service) {
        this.service = service;
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancela un riesgo puntual y sincroniza el cambio con el CORE")
    public RiesgoResponse cancelar(@PathVariable Long id) {
        return RiesgoResponse.from(service.cancelarRiesgo(id));
    }
}
