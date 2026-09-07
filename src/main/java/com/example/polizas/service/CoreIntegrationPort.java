package com.example.polizas.service;

/**
 * Puerto (arquitectura hexagonal) hacia el "servicio agnostico de edicion" disponibilizado
 * en la capa media WebLogic que mantiene actualizado el CORE de seguros. PolizaService
 * depende solo de esta interfaz; el adapter real (SOAP/REST hacia WebLogic) se conecta
 * aqui sin tocar la logica de negocio. Hoy la implementacion es {@code CoreMockAdapter}.
 */
public interface CoreIntegrationPort {
    void notificar(String evento, Long polizaId);
}
