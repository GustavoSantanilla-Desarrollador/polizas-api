# 5. Caso de liderazgo técnico

Contexto:
- 8 desarrolladores.
- 40% de deuda técnica en servicios clave.
- 10 incidentes críticos en el último mes.
- Feature de negocio exigida en 3 semanas.
- Code review inconsistente.
- 2 juniors con brechas importantes.

## Cinco prioridades durante las primeras 2 semanas

### 1. Estabilizar los servicios críticos
Analizar los 10 incidentes:
- causa raíz,
- frecuencia,
- impacto,
- servicios afectados,
- acciones preventivas.

Atacaría primero los problemas recurrentes que puedan volver a generar incidentes.

### 2. Proteger la entrega de las 3 semanas
Definir MVP, alcance congelado y dependencias. Separar "must have" de "nice to have".

### 3. Establecer estándares mínimos
- PR obligatoria.
- Code review de al menos 1 par.
- CI con build + tests.
- Definition of Done.
- análisis estático.
- manejo de secretos.
- logs estructurados.
- pruebas para cambios críticos.

### 4. Plan explícito de deuda técnica
No intentaría pagar el 40% de golpe. Clasificaría deuda por riesgo:
`crítica > alta > media > baja`.

Reservaría capacidad recurrente, por ejemplo 15–20%, para deuda y confiabilidad, revisando el porcentaje según la realidad del equipo.

### 5. Coaching y ownership
Asignaría a los juniors tareas acotadas con pairing/mentoring de seniors. Cada servicio crítico debe tener un owner técnico claro y documentación mínima.

## Organización del equipo

Con 8 personas:
- 1 líder técnico / arquitecto.
- 2 squads pequeños orientados a flujo de valor.
- 1 referente senior por squad.
- juniors distribuidos, evitando que queden aislados.
- rotación de revisión para compartir conocimiento.

No crearía silos estrictos de frontend/backend si la meta es aumentar ownership full stack.

## Métricas

Usaría métricas DORA:
- Deployment Frequency.
- Lead Time for Changes.
- Change Failure Rate.
- Mean Time to Recovery.

Además:
- defectos escapados a producción,
- incidentes por servicio,
- cobertura de pruebas donde sea relevante,
- tiempo de revisión de PR,
- PRs bloqueadas,
- disponibilidad,
- p95 de APIs,
- porcentaje de deuda crítica resuelta.

La métrica no debe convertirse en ranking individual de desarrolladores.

## Prácticas obligatorias

- Branch protection.
- PR + review.
- CI.
- pruebas unitarias y de integración para cambios críticos.
- lint/static analysis.
- secretos fuera del repositorio.
- versionamiento semántico/API.
- logs y correlation ID.
- ADR para decisiones arquitectónicas importantes.
- Definition of Done.
- postmortem sin culpables después de incidentes relevantes.

## Presión del negocio

La respuesta no es "no" ni "entregar a cualquier costo".

Proceso:
1. Hacer visible el riesgo.
2. Presentar opciones de alcance.
3. Acordar MVP.
4. Estimar capacidad real.
5. Reservar tiempo mínimo para calidad.
6. Hacer entregas incrementales.
7. Monitorear métricas y rollback.

Mensaje ejecutivo:
> Podemos entregar en tres semanas si congelamos el MVP acordado, reducimos alcance no esencial y mantenemos los controles mínimos de seguridad, pruebas y observabilidad. Si se agrega alcance, debemos mover fecha o reasignar capacidad; no ocultaría el riesgo técnico.
