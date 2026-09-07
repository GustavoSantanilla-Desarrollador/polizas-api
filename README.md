# Prueba Técnica — Senior / Tech Lead Full Stack
### Gestión de Pólizas de Arrendamiento y Riesgos

**Gustavo Santanilla** · Repositorio: `GustavoSantanilla-Desarrollador/polizas-api`

| Módulo | Contenido | Ubicación |
|---|---|---|
| 0 — Checklist de entrega | Pasos para publicar y guion de defensa oral (5 min) | [`docs/00-checklist-entrega.md`](docs/00-checklist-entrega.md) |
| 1 — Diseño de Sistema | Arquitectura hexagonal, patrones, modelo de datos, aspectos transversales | [`docs/01-arquitectura.md`](docs/01-arquitectura.md) |
| 2 — Contrato API | Endpoints, request/response, códigos de error | [`docs/02-api.md`](docs/02-api.md) |
| 2 — Prueba Técnica Práctica | API Spring Boot funcional (este proyecto) | Código fuente en `src/` |
| 3 — Conocimientos en BBDD | Optimización de la consulta SQL | [`docs/03-bbdd.md`](docs/03-bbdd.md) |
| 4 — Versionamiento (Git) | Estrategia de `cherry-pick` | [`docs/04-git.md`](docs/04-git.md) |
| 5 — Liderazgo Técnico | Caso de liderazgo y gestión de equipo | [`docs/05-liderazgo.md`](docs/05-liderazgo.md) |

---

## Módulo 2 — API de Gestión de Pólizas

### Stack
Java 17 · Spring Boot 3.3 · Spring Data JPA · H2 (en memoria) · Bean Validation · springdoc-openapi (Swagger UI) · Actuator · JUnit 5 + MockMvc · Docker.

### Arquitectura del código
```
domain/       → Poliza, Riesgo (entidades JPA con las reglas de negocio como metodos:
                renovar(), cancelar(), agregarRiesgo(), crearRiesgoInicial())
              → TipoPoliza, EstadoPoliza, EstadoRiesgo (enums)
service/      → PolizaService: orquesta repositorios y el puerto CORE (sin logica de negocio)
              → CoreIntegrationPort: puerto hacia el CORE/WebLogic
              → CoreMockAdapter: implementacion mock (solo loguea)
repository/   → PolizaRepository, RiesgoRepository (Spring Data JPA)
web/          → PolizaController, RiesgoController, CoreMockController
              → GlobalExceptionHandler (404/409/400/500 consistentes)
              → ApiKeyFilter (seguridad minima por header)
              → dto/ (records de request/response)
config/       → DataInitializer (datos de ejemplo), OpenApiConfig (Swagger + auth)
exception/    → NotFoundException (404), BusinessException (409)
```

### Cómo ejecutar

**Opción A — Local con Maven**
```bash
mvn spring-boot:run
```

**Opción B — Docker (un solo comando, sin instalar Java/Maven)**
```bash
docker compose up --build
```

La API queda en `http://localhost:8080`.
Swagger UI: `http://localhost:8080/swagger-ui.html` — usa el botón **Authorize** para pegar el `x-api-key` una sola vez y probar todos los endpoints desde ahí.
Consola H2: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:polizasdb`, usuario `sa`, sin password).
Health check: `http://localhost:8080/actuator/health` (no requiere `x-api-key`).

Al arrancar se cargan 2 pólizas de ejemplo: `POL-0001` (colectiva, 2 riesgos) y `POL-0002` (individual, 1 riesgo).

### Cómo ejecutar las pruebas
```bash
mvn test
```
Incluye pruebas de dominio (reglas de negocio puras, sin mocks) y pruebas de integración (contexto Spring completo, filtro de seguridad, datos de ejemplo).

### Seguridad
Todos los endpoints (excepto Swagger, `/v3/api-docs` y `/actuator/health`) requieren:
```
x-api-key: 123456
```
Configurable vía variable de entorno `APP_API_KEY` (ver `docker-compose.yml`).

### Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/polizas?tipo=&estado=` | Lista pólizas, filtros opcionales |
| GET | `/polizas/{id}/riesgos` | Lista los riesgos de una póliza |
| POST | `/polizas/{id}/renovar` | Renueva: ajusta canon/prima según el IPC de la póliza |
| POST | `/polizas/{id}/cancelar` | Cancela la póliza y todos sus riesgos activos |
| POST | `/polizas/{id}/riesgos` | Agrega un riesgo (solo `COLECTIVA`) |
| POST | `/riesgos/{id}/cancelar` | Cancela un riesgo puntual |
| POST | `/core-mock/evento` | Mock del servicio agnóstico de edición (capa WebLogic) |

### Reglas de negocio (viven en el dominio, no en el service)
- INDIVIDUAL exige exactamente 1 riesgo, creado junto con la póliza.
- Solo COLECTIVA admite agregar riesgos adicionales después de creada.
- No se puede renovar ni volver a cancelar una póliza ya cancelada.
- Cancelar una póliza cancela todos sus riesgos activos.
- `@Version` (locking optimista) evita que dos operaciones concurrentes sobre la misma póliza se pisen.

### Ejemplos con `curl`
```bash
# Listar pólizas colectivas activas
curl -H "x-api-key: 123456" "http://localhost:8080/polizas?tipo=COLECTIVA&estado=ACTIVA"

# Riesgos de la póliza 1
curl -H "x-api-key: 123456" http://localhost:8080/polizas/1/riesgos

# Renovar la póliza 2
curl -X POST -H "x-api-key: 123456" http://localhost:8080/polizas/2/renovar

# Cancelar la póliza 1 (cancela también sus riesgos)
curl -X POST -H "x-api-key: 123456" http://localhost:8080/polizas/1/cancelar

# Agregar un riesgo a la póliza colectiva 1
curl -X POST -H "x-api-key: 123456" -H "Content-Type: application/json" \
  -d '{"arrendatario":"Maria Lopez","direccion":"Calle 50 #10-05","canonMensual":2100000}' \
  http://localhost:8080/polizas/1/riesgos

# Cancelar el riesgo 1
curl -X POST -H "x-api-key: 123456" http://localhost:8080/riesgos/1/cancelar

# Simular evento hacia el CORE
curl -X POST -H "x-api-key: 123456" -H "Content-Type: application/json" \
  -d '{"evento":"ACTUALIZACION","polizaId":1}' \
  http://localhost:8080/core-mock/evento
```

### CI
`.github/workflows/ci.yml` corre `mvn clean verify` en cada push/PR a `main` (compila + ejecuta todas las pruebas).

### Decisiones de diseño relevantes
- **Rich Domain Model**: las reglas de negocio son métodos de `Poliza`/`Riesgo`, no lógica dispersa en el service — hace imposible dejar una entidad en estado inconsistente desde ningún caller.
- **Puerto/Adapter para el CORE**: `CoreIntegrationPort` aísla la integración con WebLogic; hoy `CoreMockAdapter` solo loguea, mañana se reemplaza por un cliente SOAP/REST real sin tocar `PolizaService`.
- **H2 en memoria**: para que el evaluador levante el proyecto con un solo comando (`mvn spring-boot:run` o `docker compose up`), sin infraestructura externa. El modelo es portable a Oracle/PostgreSQL cambiando solo el datasource.
- **IPC por póliza, no global**: cada póliza guarda su propio `porcentajeIpc`, reflejando que en la práctica distintas pólizas pueden negociar distintos ajustes anuales.
