package com.example.polizas.service;

import com.example.polizas.domain.*;
import com.example.polizas.exception.NotFoundException;
import com.example.polizas.repository.PolizaRepository;
import com.example.polizas.repository.RiesgoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Capa de aplicacion: orquesta repositorios y el puerto CORE. Las reglas de negocio
 * (validaciones de estado, cardinalidad de riesgos, calculo de renovacion) viven en las
 * entidades de dominio ({@link Poliza}, {@link Riesgo}), no aqui.
 */
@Service
public class PolizaService {

    private final PolizaRepository polizaRepository;
    private final RiesgoRepository riesgoRepository;
    private final CoreIntegrationPort coreIntegrationPort;

    public PolizaService(PolizaRepository polizaRepository,
                          RiesgoRepository riesgoRepository,
                          CoreIntegrationPort coreIntegrationPort) {
        this.polizaRepository = polizaRepository;
        this.riesgoRepository = riesgoRepository;
        this.coreIntegrationPort = coreIntegrationPort;
    }

    @Transactional(readOnly = true)
    public List<Poliza> listar(TipoPoliza tipo, EstadoPoliza estado) {
        if (tipo != null && estado != null) {
            return polizaRepository.findByTipoAndEstado(tipo, estado);
        } else if (tipo != null) {
            return polizaRepository.findByTipo(tipo);
        } else if (estado != null) {
            return polizaRepository.findByEstado(estado);
        }
        return polizaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Poliza obtener(Long id) {
        return polizaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Poliza " + id + " no encontrada"));
    }

    @Transactional(readOnly = true)
    public List<Riesgo> listarRiesgos(Long polizaId) {
        obtener(polizaId); // valida existencia, lanza 404 si no existe
        return riesgoRepository.findByPolizaId(polizaId);
    }

    @Transactional
    public Poliza renovar(Long id) {
        Poliza poliza = obtener(id);
        poliza.renovar();
        Poliza guardada = polizaRepository.save(poliza);
        coreIntegrationPort.notificar("RENOVACION", guardada.getId());
        return guardada;
    }

    @Transactional
    public Poliza cancelar(Long id) {
        Poliza poliza = obtener(id);
        poliza.cancelar();
        Poliza guardada = polizaRepository.save(poliza);
        coreIntegrationPort.notificar("CANCELACION", guardada.getId());
        return guardada;
    }

    @Transactional
    public Riesgo agregarRiesgo(Long polizaId, String arrendatario, String direccion, BigDecimal canonMensual) {
        Poliza poliza = obtener(polizaId);
        Riesgo riesgo = poliza.agregarRiesgo(arrendatario, direccion, canonMensual);
        polizaRepository.save(poliza);
        coreIntegrationPort.notificar("ACTUALIZACION", poliza.getId());
        return riesgo;
    }

    @Transactional
    public Riesgo cancelarRiesgo(Long riesgoId) {
        Riesgo riesgo = riesgoRepository.findById(riesgoId)
                .orElseThrow(() -> new NotFoundException("Riesgo " + riesgoId + " no encontrado"));
        riesgo.cancelar();
        Riesgo guardado = riesgoRepository.save(riesgo);
        coreIntegrationPort.notificar("ACTUALIZACION", riesgo.getPoliza().getId());
        return guardado;
    }
}
