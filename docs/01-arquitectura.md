# 1. Arquitectura de alto nivel

## Objetivo

Separar el dominio de pólizas/riesgos de los mecanismos de persistencia, exposición HTTP e integración con el CORE legado. La solución propuesta puede comenzar como un **modular monolith** bien delimitado y evolucionar a microservicios si el volumen y la autonomía de los equipos lo justifican.

La prueba solicita componentes de Servicio de Pólizas, Servicio de Riesgos, Notificaciones, Adapter CORE, Base de Datos y API Gateway. En la implementación práctica se mantienen como módulos/capas dentro de una misma aplicación para reducir complejidad, pero los límites de dominio quedan explícitos.

## Componentes

1. **API Gateway**
   - Autenticación/autorización.
   - Rate limiting.
   - Correlation ID.
   - TLS.
   - Routing y versionamiento.

2. **Servicio de Pólizas**
   - Crear/consultar/modificar pólizas.
   - Renovación.
   - Cancelación.
   - Reglas de estado.

3. **Servicio de Riesgos**
   - Alta de riesgos en colectivas.
   - Cancelación de riesgos.
   - Validación de cardinalidad.

4. **Servicio de Notificaciones**
   - Publicación de eventos.
   - Email/SMS asíncrono.
   - Reintentos y DLQ.

5. **Adapter CORE**
   - Aísla el protocolo/contrato legado.
   - Timeout, retry controlado, circuit breaker.
   - Idempotencia.
   - Auditoría de intentos.

6. **Persistencia**
   - BD transaccional.
   - Índices sobre consultas frecuentes.
   - Migraciones versionadas.

## Patrones seleccionados

### 1. Arquitectura Hexagonal
**Por qué:** las reglas de negocio no deben depender de Spring, JPA, WebLogic ni del CORE. `CoreIntegrationPort` es un puerto y `CoreMockAdapter` es un adaptador.

Beneficio: permite sustituir el mock por el adaptador real sin modificar el caso de uso.

### 2. Event-Driven
**Por qué:** creación y renovación requieren notificaciones. No conviene bloquear la transacción principal esperando SMS/email.

Flujo:
`PolizaService -> evento -> broker -> Notification Service`

En producción usaría Outbox Pattern para evitar perder eventos entre commit de BD y publicación.

### 3. API Gateway
**Por qué:** centraliza seguridad, rate limiting, observabilidad, versionamiento y políticas de acceso.

La implementación del challenge mantiene el API-key dentro de la aplicación para ser ejecutable en 2 horas; en producción la validación de seguridad debería vivir principalmente en el gateway/IdP.

## Resiliencia

- Timeouts explícitos.
- Retry únicamente para errores transitorios.
- Exponential backoff.
- Circuit breaker.
- Idempotency key en operaciones externas.
- Bulkhead para aislar dependencias.
- Outbox + consumidor idempotente.
- Dead Letter Queue para eventos fallidos.
- Health/readiness/liveness.
- Backups y recuperación probada.

## Escalabilidad

- API stateless.
- Escalamiento horizontal.
- Pool de conexiones controlado.
- Índices y paginación.
- Cache solo para datos de lectura apropiados.
- Broker para desacoplar notificaciones.
- Separar lectura/escritura solo si la carga lo justifica (CQRS como evolución, no como complejidad inicial).

## Observabilidad

Cada request debe incluir:
- `correlationId`
- `requestId`
- `user/clientId`

Logs estructurados JSON:
- timestamp
- level
- service
- operation
- correlationId
- polizaId
- riesgoId
- outcome
- latencyMs

Métricas:
- latencia p50/p95/p99
- tasa de errores 4xx/5xx
- throughput
- renovaciones exitosas/fallidas
- errores CORE
- retries/circuit breaker
- profundidad de colas

Tracing distribuido con OpenTelemetry.

## Versionamiento

Publicaría `/api/v1/polizas`. Cambios incompatibles generan `/api/v2`.

Cambios compatibles se mantienen en la misma versión. Además:
- OpenAPI versionado.
- Política de deprecación.
- Contract tests.
- Compatibilidad hacia atrás durante ventana definida.

## Modelo conceptual

### Poliza
- id
- numero
- tipo: INDIVIDUAL | COLECTIVA
- estado: ACTIVA | RENOVADA | CANCELADA
- fechaInicio
- fechaFin
- canonMensual
- prima
- porcentajeIpc
- version

### Riesgo
- id
- polizaId
- arrendatario
- direccion
- canonMensual
- estado: ACTIVO | CANCELADO
- fechaCreacion
- fechaCancelacion

### Relaciones
- Colectiva 1:N Riesgo
- Individual 1:1 Riesgo

## Regla financiera

La prueba indica que la prima corresponde al canon mensual por número de meses de vigencia. Para renovación se ajusta canon según IPC.

En la implementación:
`nuevoCanon = canonActual * (1 + ipc)`
`nuevaPrima = nuevoCanon * mesesVigencia`

Se redondea a 2 decimales.

## Consistencia con CORE

La operación local y la actualización del CORE deben tener una estrategia explícita. Para el challenge se registra el intento mediante mock. En producción:

1. Persistir cambio + evento Outbox en una misma transacción.
2. Worker publica/consume el evento.
3. Adapter WebLogic/CORE procesa.
4. Se registra resultado.
5. Reintentos ante fallos transitorios.
6. DLQ y alerta ante fallo persistente.

Así se evita una transacción distribuida frágil entre la BD y el legado.


## Decisión de entrega para una prueba de 2 horas

No recomiendo implementar cinco microservicios físicos para esta evaluación: aumentaría infraestructura y boilerplate y reduciría el tiempo disponible para demostrar dominio, pruebas y reglas de negocio.

La mejor señal de liderazgo técnico es explicar el límite arquitectónico y entregar un modular monolith ejecutable. Después de validar volumen, ownership y necesidades de despliegue, los módulos de Riesgos y Notificaciones pueden extraerse sin cambiar los contratos de dominio.
