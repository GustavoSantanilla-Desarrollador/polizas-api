# 3. Optimización de la consulta de órdenes

Consulta:
```sql
SELECT o.order_id, o.order_date, c.customer_name, o.total_amount
FROM orders o
JOIN customers c ON o.customer_id = c.customer_id
WHERE c.country = 'México';
```

## Estrategia 1 — Índices

Crear índice sobre `customers(country, customer_id)` y evaluar `orders(customer_id)`.

```sql
CREATE INDEX idx_customers_country_customer
ON customers(country, customer_id);

CREATE INDEX idx_orders_customer
ON orders(customer_id);
```

El índice compuesto permite localizar clientes del país y tener disponible la clave de join.

## Estrategia 2 — Revisar el plan de ejecución

Usaría `EXPLAIN` / `EXPLAIN ANALYZE` y estadísticas actualizadas para confirmar si el optimizador hace:
- full scan innecesario,
- nested loop costoso,
- estimaciones incorrectas,
- falta de uso de índices.

No se debe crear índices a ciegas: se valida con plan y métricas.

## Estrategia 3 — Particionamiento de orders

Con 10 millones de registros, si las consultas reales también filtran por fecha, consideraría particionar `orders` por `order_date` (por mes/año). Esto permite partition pruning.

Si la consulta siempre fuera únicamente por país, el particionamiento por fecha no resuelve directamente el filtro por país; debe justificarse con el patrón real de consultas.

## Otras medidas

- Materialized view si el reporte es frecuente y tolera cierta latencia.
- Caché para resultados de baja variabilidad.
- Réplica de lectura para carga analítica.
- Evitar `SELECT *`.
- Paginación cuando el consumidor no necesita todos los registros.
- Mantener estadísticas actualizadas.
- Revisar cardinalidad y distribución de `country`.
