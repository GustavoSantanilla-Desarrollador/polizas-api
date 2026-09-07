package com.example.polizas;

import com.example.polizas.domain.*;
import com.example.polizas.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de las reglas de negocio directamente sobre el dominio (Poliza/Riesgo).
 * Al vivir las reglas en las entidades, no se necesitan mocks de repositorios: se
 * instancian objetos de dominio puros y se verifica su comportamiento.
 */
class PolizaDomainTest {

    private Poliza polizaIndividual() {
        Poliza p = Poliza.crear("POL-TEST-1", TipoPoliza.INDIVIDUAL,
                LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1),
                BigDecimal.valueOf(1000000), BigDecimal.valueOf(0.10));
        p.crearRiesgoInicial("Arrendatario Test", "Direccion Test", BigDecimal.valueOf(1000000));
        return p;
    }

    private Poliza polizaColectiva() {
        return Poliza.crear("POL-TEST-2", TipoPoliza.COLECTIVA,
                LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1),
                BigDecimal.valueOf(2000000), BigDecimal.valueOf(0.08));
    }

    @Test
    void crearCalculaPrimaComoCanonPorMeses() {
        Poliza p = polizaIndividual();
        assertEquals(0, p.getPrima().compareTo(BigDecimal.valueOf(12000000).setScale(2)));
    }

    @Test
    void individualNoPermiteMasDeUnRiesgo() {
        Poliza p = polizaIndividual();
        assertThrows(BusinessException.class, () ->
                p.crearRiesgoInicial("Otro", "Otra direccion", BigDecimal.valueOf(500000)));
    }

    @Test
    void soloColectivaPermiteAgregarRiesgoAdicional() {
        Poliza individual = polizaIndividual();
        assertThrows(BusinessException.class, () ->
                individual.agregarRiesgo("X", "Y", BigDecimal.valueOf(100000)));

        Poliza colectiva = polizaColectiva();
        assertDoesNotThrow(() -> colectiva.agregarRiesgo("X", "Y", BigDecimal.valueOf(100000)));
        assertEquals(1, colectiva.getRiesgos().size());
    }

    @Test
    void noPermiteRenovarPolizaCancelada() {
        Poliza p = polizaIndividual();
        p.cancelar();
        assertThrows(BusinessException.class, p::renovar);
    }

    @Test
    void renovarIncrementaCanonYPrimaSegunIpc() {
        Poliza p = polizaIndividual();
        BigDecimal canonAntes = p.getCanonMensual();

        p.renovar();

        assertEquals(EstadoPoliza.RENOVADA, p.getEstado());
        assertTrue(p.getCanonMensual().compareTo(canonAntes) > 0);
        assertEquals(0, p.getCanonMensual().compareTo(BigDecimal.valueOf(1100000).setScale(2)));
    }

    @Test
    void cancelarPolizaCancelaTodosSusRiesgosActivos() {
        Poliza colectiva = polizaColectiva();
        colectiva.agregarRiesgo("A", "DirA", BigDecimal.valueOf(100000));
        colectiva.agregarRiesgo("B", "DirB", BigDecimal.valueOf(200000));

        colectiva.cancelar();

        assertEquals(EstadoPoliza.CANCELADA, colectiva.getEstado());
        assertTrue(colectiva.getRiesgos().stream().allMatch(r -> r.getEstado() == EstadoRiesgo.CANCELADO));
    }

    @Test
    void noPermiteCancelarUnaPolizaYaCancelada() {
        Poliza p = polizaIndividual();
        p.cancelar();
        assertThrows(BusinessException.class, p::cancelar);
    }

    @Test
    void noPermiteCancelarUnRiesgoYaCancelado() {
        Poliza colectiva = polizaColectiva();
        Riesgo riesgo = colectiva.agregarRiesgo("A", "DirA", BigDecimal.valueOf(100000));
        riesgo.cancelar();
        assertThrows(BusinessException.class, riesgo::cancelar);
    }
}
