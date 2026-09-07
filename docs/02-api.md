# 2. Contrato API

Header obligatorio para todos los endpoints:
`x-api-key: 123456`

## GET /polizas

Query params opcionales:
- `tipo`: INDIVIDUAL | COLECTIVA
- `estado`: ACTIVA | RENOVADA | CANCELADA

Respuesta `200`:
```json
[
  {
    "id": 1,
    "numero": "POL-0001",
    "tipo": "COLECTIVA",
    "estado": "ACTIVA",
    "fechaInicio": "2026-01-01",
    "fechaFin": "2027-01-01",
    "canonMensual": 2500000,
    "prima": 30000000,
    "porcentajeIpc": 0.08
  }
]
```

## GET /polizas/{id}/riesgos

Devuelve los riesgos asociados.

## POST /polizas/{id}/renovar

- Rechaza póliza CANCELADA.
- Aumenta canon según IPC.
- Recalcula prima.
- Cambia estado a RENOVADA.
- Notifica evento de renovación.
- Intenta sincronización con CORE.

## POST /polizas/{id}/cancelar

- Cambia póliza a CANCELADA.
- Cancela todos sus riesgos.
- Intenta sincronización con CORE.

## POST /polizas/{id}/riesgos

Solo permitido para COLECTIVA.

Body:
```json
{
  "arrendatario": "Ana Pérez",
  "direccion": "Calle 100 # 10-20",
  "canonMensual": 2500000
}
```

## POST /riesgos/{id}/cancelar

Cancela el riesgo y sincroniza el cambio con CORE.

## POST /core-mock/evento

Body:
```json
{
  "evento": "ACTUALIZACION",
  "polizaId": 555
}
```

El mock únicamente registra en logs el intento de envío al CORE.

## Errores

- `400` validación / regla de negocio.
- `401` API key ausente o incorrecta.
- `404` entidad inexistente.
- `409` conflicto de estado.
- `500` error inesperado.

Ejemplo:
```json
{
  "timestamp": "2026-09-03T18:00:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "Una póliza cancelada no puede renovarse",
  "path": "/polizas/1/renovar"
}
```
