package com.example.polizas.config;

import com.example.polizas.domain.Poliza;
import com.example.polizas.domain.TipoPoliza;
import com.example.polizas.repository.PolizaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Carga datos de ejemplo al iniciar, usando unicamente la API publica del dominio
 * (Poliza.crear / crearRiesgoInicial / agregarRiesgo) — nunca reflection ni acceso a
 * campos privados, para que la inicializacion respete las mismas invariantes que
 * cualquier otro caller.
 */
@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner init(PolizaRepository repo) {
        return args -> {
            if (repo.count() > 0) {
                return;
            }

            Poliza colectiva = Poliza.crear(
                    "POL-0001", TipoPoliza.COLECTIVA,
                    LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1),
                    BigDecimal.valueOf(2500000), BigDecimal.valueOf(0.08));
            colectiva.agregarRiesgo("Ana Perez", "Calle 100 # 10-20, Bogota", BigDecimal.valueOf(2500000));
            colectiva.agregarRiesgo("Carlos Ruiz", "Carrera 15 # 80-30, Bogota", BigDecimal.valueOf(1900000));
            repo.save(colectiva);

            Poliza individual = Poliza.crear(
                    "POL-0002", TipoPoliza.INDIVIDUAL,
                    LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1),
                    BigDecimal.valueOf(1800000), BigDecimal.valueOf(0.08));
            individual.crearRiesgoInicial("Juan Perez", "Avenida 68 # 20-30, Bogota", BigDecimal.valueOf(1800000));
            repo.save(individual);

            log.info("========================================");
            log.info("DATOS INICIALES CARGADOS");
            log.info("POL-0001 - COLECTIVA (2 riesgos)");
            log.info("POL-0002 - INDIVIDUAL (1 riesgo)");
            log.info("========================================");
        };
    }
}
