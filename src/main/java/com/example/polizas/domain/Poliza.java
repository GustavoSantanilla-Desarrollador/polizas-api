package com.example.polizas.domain;

import com.example.polizas.exception.BusinessException;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Poliza de arrendamiento. Individual: exactamente 1 riesgo, creado junto con la poliza.
 * Colectiva: puede recibir riesgos adicionales despues de creada via {@link #agregarRiesgo}.
 * Las reglas de negocio (renovar, cancelar, agregar riesgo) viven aqui, no en el service,
 * para que sea imposible dejar la entidad en un estado inconsistente desde ningun caller.
 */
@Entity
@Table(name = "polizas")
public class Poliza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPoliza tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPoliza estado;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal canonMensual;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal prima;

    /** IPC especifico de esta poliza, usado para el ajuste del canon en cada renovacion. */
    @Column(nullable = false, precision = 8, scale = 5)
    private BigDecimal porcentajeIpc;

    @Version
    private Long version;

    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Riesgo> riesgos = new ArrayList<>();

    protected Poliza() {
    }

    /**
     * Crea una poliza INDIVIDUAL junto con su unico riesgo, o una poliza COLECTIVA sin
     * riesgos iniciales (se agregan despues con {@link #agregarRiesgo}).
     */
    public static Poliza crear(String numero, TipoPoliza tipo, LocalDate fechaInicio, LocalDate fechaFin,
                                BigDecimal canonMensual, BigDecimal porcentajeIpc) {
        Poliza p = new Poliza();
        p.numero = numero;
        p.tipo = tipo;
        p.estado = EstadoPoliza.ACTIVA;
        p.fechaInicio = fechaInicio;
        p.fechaFin = fechaFin;
        p.canonMensual = canonMensual;
        p.porcentajeIpc = porcentajeIpc;
        p.prima = calcularPrima(canonMensual, fechaInicio, fechaFin);
        return p;
    }

    /** Riesgo inicial de una poliza INDIVIDUAL (o el primero de una COLECTIVA, si aplica). */
    public Riesgo crearRiesgoInicial(String arrendatario, String direccion, BigDecimal canonMensual) {
        if (tipo == TipoPoliza.INDIVIDUAL && !riesgos.isEmpty()) {
            throw new BusinessException("Una poliza individual solo admite un riesgo");
        }
        Riesgo riesgo = Riesgo.crear(this, arrendatario, direccion, canonMensual);
        riesgos.add(riesgo);
        return riesgo;
    }

    /** Agrega un riesgo adicional. Solo permitido para polizas COLECTIVA activas o renovadas. */
    public Riesgo agregarRiesgo(String arrendatario, String direccion, BigDecimal canonMensual) {
        if (tipo != TipoPoliza.COLECTIVA) {
            throw new BusinessException("Solo las polizas colectivas admiten agregar riesgos adicionales");
        }
        if (estado == EstadoPoliza.CANCELADA) {
            throw new BusinessException("No se pueden agregar riesgos a una poliza cancelada");
        }
        Riesgo riesgo = Riesgo.crear(this, arrendatario, direccion, canonMensual);
        riesgos.add(riesgo);
        return riesgo;
    }

    /** Renueva: ajusta canon/prima segun el IPC de la poliza y extiende la vigencia. */
    public void renovar() {
        if (estado == EstadoPoliza.CANCELADA) {
            throw new BusinessException("Una poliza cancelada no puede renovarse");
        }
        long meses = mesesVigencia();
        BigDecimal factor = BigDecimal.ONE.add(porcentajeIpc);
        canonMensual = canonMensual.multiply(factor).setScale(2, RoundingMode.HALF_UP);
        prima = canonMensual.multiply(BigDecimal.valueOf(meses)).setScale(2, RoundingMode.HALF_UP);
        fechaInicio = fechaFin;
        fechaFin = fechaFin.plusMonths(meses);
        estado = EstadoPoliza.RENOVADA;
    }

    /** Cancela la poliza y en cascada todos sus riesgos activos. */
    public void cancelar() {
        if (estado == EstadoPoliza.CANCELADA) {
            throw new BusinessException("La poliza " + numero + " ya se encuentra cancelada");
        }
        estado = EstadoPoliza.CANCELADA;
        riesgos.stream()
                .filter(r -> r.getEstado() == EstadoRiesgo.ACTIVO)
                .forEach(Riesgo::cancelar);
    }

    private long mesesVigencia() {
        return Math.max(1, ChronoUnit.MONTHS.between(fechaInicio, fechaFin));
    }

    private static BigDecimal calcularPrima(BigDecimal canonMensual, LocalDate inicio, LocalDate fin) {
        long meses = Math.max(1, ChronoUnit.MONTHS.between(inicio, fin));
        return canonMensual.multiply(BigDecimal.valueOf(meses)).setScale(2, RoundingMode.HALF_UP);
    }

    public Long getId() {
        return id;
    }

    public String getNumero() {
        return numero;
    }

    public TipoPoliza getTipo() {
        return tipo;
    }

    public EstadoPoliza getEstado() {
        return estado;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public BigDecimal getCanonMensual() {
        return canonMensual;
    }

    public BigDecimal getPrima() {
        return prima;
    }

    public BigDecimal getPorcentajeIpc() {
        return porcentajeIpc;
    }

    public List<Riesgo> getRiesgos() {
        return riesgos;
    }
}
