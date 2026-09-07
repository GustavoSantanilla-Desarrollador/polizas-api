package com.example.polizas.web;

import com.example.polizas.web.dto.CoreEventRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Mock del servicio agnostico de edicion expuesto por la capa media WebLogic. Unico
 * proposito segun el enunciado: registrar en logs que la operacion se intento enviar
 * al CORE transaccional legado.
 */
@RestController
@RequestMapping("/core-mock")
@Tag(name = "Core Mock", description = "Simula el servicio agnostico de edicion (capa WebLogic)")
public class CoreMockController {

    private static final Logger log = LoggerFactory.getLogger(CoreMockController.class);

    @PostMapping("/evento")
    @Operation(summary = "Registra en logs un evento simulado hacia el CORE")
    public void evento(@Valid @RequestBody CoreEventRequest request) {
        log.info("[CORE-MOCK] evento={} polizaId={} recibido", request.evento(), request.polizaId());
    }
}
