package com.example.polizas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Implementacion mock de {@link CoreIntegrationPort} para esta prueba: solo registra en
 * logs el intento de sincronizacion. En produccion se reemplaza por un adapter real
 * (cliente SOAP/REST hacia WebLogic) con reintentos y circuit breaker, sin tocar
 * PolizaService ni ningun otro consumidor del puerto.
 */
@Component
public class CoreMockAdapter implements CoreIntegrationPort {

    private static final Logger log = LoggerFactory.getLogger(CoreMockAdapter.class);

    @Override
    public void notificar(String evento, Long polizaId) {
        log.info("[CORE-SYNC] evento={} polizaId={} -> enviado (mock) al CORE transaccional legado",
                evento, polizaId);
    }
}
