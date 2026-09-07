package com.example.polizas.repository;

import com.example.polizas.domain.EstadoPoliza;
import com.example.polizas.domain.Poliza;
import com.example.polizas.domain.TipoPoliza;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PolizaRepository extends JpaRepository<Poliza, Long> {

    List<Poliza> findByTipo(TipoPoliza tipo);

    List<Poliza> findByEstado(EstadoPoliza estado);

    List<Poliza> findByTipoAndEstado(TipoPoliza tipo, EstadoPoliza estado);
}
