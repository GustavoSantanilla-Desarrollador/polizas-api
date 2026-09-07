package com.example.polizas.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "riesgos")
public class Riesgo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poliza_id", nullable = false)
    private Poliza poliza;

    @Column(nullable = false)
    private String arrendatario;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal canonMensual;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRiesgo estado;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaCancelacion;

    protected Riesgo() {
    }

    static Riesgo crear(Poliza poliza, String arrendatario, String direccion, BigDecimal canonMensual) {
        Riesgo r = new Riesgo();
        r.poliza = poliza;
        r.arrendatario = arrendatario;
        r.direccion = direccion;
        r.canonMensual = canonMensual;
        r.estado = EstadoRiesgo.ACTIVO;
        r.fechaCreacion = LocalDateTime.now();
        return r;
    }

    public void cancelar() {
        if (estado == EstadoRiesgo.CANCELADO) {
            throw new com.example.polizas.exception.BusinessException(
                    "El riesgo " + id + " ya se encuentra cancelado");
        }
        estado = EstadoRiesgo.CANCELADO;
        fechaCancelacion = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Poliza getPoliza() {
        return poliza;
    }

    public String getArrendatario() {
        return arrendatario;
    }

    public String getDireccion() {
        return direccion;
    }

    public BigDecimal getCanonMensual() {
        return canonMensual;
    }

    public EstadoRiesgo getEstado() {
        return estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaCancelacion() {
        return fechaCancelacion;
    }
}
