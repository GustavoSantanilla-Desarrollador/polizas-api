package com.example.polizas.web;

import com.example.polizas.domain.EstadoPoliza;
import com.example.polizas.domain.TipoPoliza;
import com.example.polizas.service.PolizaService;
import com.example.polizas.web.dto.PolizaResponse;
import com.example.polizas.web.dto.RiesgoRequest;
import com.example.polizas.web.dto.RiesgoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/polizas")
@Tag(name = "Polizas", description = "Gestion de polizas de arrendamiento")
public class PolizaController {

    private final PolizaService service;

    public PolizaController(PolizaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista polizas, con filtros opcionales por tipo y estado")
    public List<PolizaResponse> listar(@RequestParam(required = false) TipoPoliza tipo,
                                        @RequestParam(required = false) EstadoPoliza estado) {
        return service.listar(tipo, estado).stream().map(PolizaResponse::from).toList();
    }

    @GetMapping("/{id}/riesgos")
    @Operation(summary = "Lista los riesgos asociados a una poliza")
    public List<RiesgoResponse> riesgos(@PathVariable Long id) {
        return service.listarRiesgos(id).stream().map(RiesgoResponse::from).toList();
    }

    @PostMapping("/{id}/renovar")
    @Operation(summary = "Renueva la poliza: ajusta canon/prima segun IPC y extiende vigencia")
    public PolizaResponse renovar(@PathVariable Long id) {
        return PolizaResponse.from(service.renovar(id));
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancela la poliza y todos sus riesgos activos")
    public PolizaResponse cancelar(@PathVariable Long id) {
        return PolizaResponse.from(service.cancelar(id));
    }

    @PostMapping("/{id}/riesgos")
    @Operation(summary = "Agrega un riesgo adicional (solo permitido para polizas COLECTIVA)")
    public ResponseEntity<RiesgoResponse> agregar(@PathVariable Long id,
                                                   @Valid @RequestBody RiesgoRequest request) {
        RiesgoResponse response = RiesgoResponse.from(
                service.agregarRiesgo(id, request.arrendatario(), request.direccion(), request.canonMensual()));
        return ResponseEntity.ok(response);
    }
}
