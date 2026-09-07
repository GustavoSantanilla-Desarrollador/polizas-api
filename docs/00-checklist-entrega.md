# Checklist para subir a GitHub

- [ ] Ejecutar `mvn test`
- [ ] Ejecutar `mvn package`
- [ ] Probar endpoints con Swagger/curl
- [ ] Revisar reglas de negocio
- [ ] Revisar README
- [ ] Revisar diagramas
- [ ] Revisar que no existan secretos
- [ ] Crear repositorio GitHub privado/público según instrucciones de la prueba
- [ ] `git init`
- [ ] `git add .`
- [ ] `git commit -m "feat: implement policy management API"`
- [ ] `git branch -M main`
- [ ] `git remote add origin <REPO_URL>`
- [ ] `git push -u origin main`

## Defensa oral de 5 minutos

1. Empecé por separar dominio, adapters y entrada HTTP.
2. Para el challenge usé un modular monolith para entregar rápido sin perder límites arquitectónicos.
3. Hexagonal desacopla el CORE legado.
4. Event-driven desacopla notificaciones; en producción agregaría Outbox.
5. Las reglas críticas viven en el dominio/servicio y tienen pruebas.
6. La solución escala horizontalmente porque la API es stateless.
7. La resiliencia se completa con timeout, retry, circuit breaker y DLQ.
8. GitHub Actions automatiza build y tests.
