# Trazabilidad: escenario, prueba y captura

Relación entre cada escenario Gherkin de las historias de usuario ([historias.md](historias.md)), la prueba automática que lo verifica y la captura que lo evidencia en el documento final (Parte III, sección 5, «Verificación de las historias implementadas»).

- **PruebasFlujo:** 99 comprobaciones de las reglas de negocio sobre los servicios.
- **PruebasApi:** 43 comprobaciones del contrato REST.
- Con `PRUEBAS_BD=postgres` las mismas pruebas se ejecutan contra un PostgreSQL real.
- **Captura:** número de figura en el documento final. «—» indica que el escenario se verifica solo con pruebas automáticas.

| HU | Escenario | Prueba | Captura |
|----|-----------|--------|---------|
| HU-01 | 1. Recursos disponibles | PruebasFlujo | Figura 42 |
| HU-01 | 2. Sin recursos disponibles | PruebasFlujo | — |
| HU-01 | 3. Recurso prestado sin devolución | PruebasFlujo | Figura 53 |
| HU-02 | 1. Solicitud registrada | PruebasFlujo, PruebasApi | Figura 43 |
| HU-02 | 2. Campos obligatorios vacíos | PruebasFlujo, PruebasApi | Figura 54 |
| HU-02 | 3. Horario no válido | PruebasFlujo, PruebasApi | Figuras 55 y 56 |
| HU-03 | 1. Recurso con reserva aprobada | PruebasFlujo | Figura 57 |
| HU-03 | 2. Dos solicitudes simultáneas | PruebasFlujo (30 rondas concurrentes) | Figura 59 |
| HU-03 | 3. El mismo usuario ya tiene reserva | PruebasFlujo, PruebasApi | Figura 58 |
| HU-04 | 1. Notificación de aprobada | PruebasFlujo, PruebasApi | Figura 46 |
| HU-04 | 2. Notificación de rechazada | PruebasFlujo, PruebasApi | Figura 60 |
| HU-04 | 3. Solicitud sin respuesta | PruebasFlujo | — |
| HU-05 | 1. Préstamos con su estado | PruebasFlujo, PruebasApi | Figura 47 |
| HU-05 | 2. Sin préstamos | PruebasFlujo, PruebasApi | Figura 61 |
| HU-05 | 3. Préstamo devuelto | PruebasFlujo, PruebasApi | — |
| HU-06 | 1. Registrar un recurso | PruebasFlujo, PruebasApi | Figura 49 |
| HU-06 | 2. Modificar un recurso | PruebasFlujo, PruebasApi | — |
| HU-06 | 3. Eliminar sin trámites | PruebasFlujo, PruebasApi | — |
| HU-06 | 4. Eliminar con trámites | PruebasFlujo, PruebasApi | Figura 62 |
| HU-07 | 1. Aprobar | PruebasFlujo, PruebasApi | Figura 45 |
| HU-07 | 2. Rechazar | PruebasFlujo, PruebasApi | Figura 60 |
| HU-07 | 3. Aprobar una que se cruza | PruebasFlujo, PruebasApi | Figura 59 |
| HU-08 | 1. Registrar devolución | PruebasFlujo, PruebasApi | Figura 48 |
| HU-08 | 2. Devolución repetida | PruebasFlujo, PruebasApi | — |
| HU-08 | 3. Rol sin permiso | PruebasFlujo, PruebasApi | — |
| HU-09 | 1. Credenciales correctas | PruebasFlujo, PruebasApi | Figura 41 |
| HU-09 | 2. Credenciales incorrectas | PruebasFlujo, PruebasApi | Figura 51 |
| HU-09 | 3. Campos vacíos | PruebasFlujo, PruebasApi | Figura 52 |
| HU-10 | 1. Crear cuenta con rol | PruebasFlujo, PruebasApi | Figura 50 |
| HU-10 | 2. Correo duplicado | PruebasFlujo, PruebasApi | Figura 63 |
| HU-10 | 3. Datos incompletos | PruebasFlujo, PruebasApi | Figura 64 |

Total: 31 escenarios de 10 historias de usuario, todos verificados.
