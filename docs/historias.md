# Historias de usuario — Sistema de Préstamo de Recursos de Laboratorio

Historias de usuario del MVP, tomadas del documento final (Parte I, sección 3). Cada historia sigue la estructura Como / Quiero / Para y sus criterios de aceptación están en formato Gherkin (Dado / Cuando / Entonces / Y).

La verificación de cada escenario está en [trazabilidad.md](trazabilidad.md).

| HU | Nombre |
|----|--------|
| HU-01 | Consultar disponibilidad de recursos |
| HU-02 | Solicitar préstamo de un recurso |
| HU-03 | Bloquear solicitudes en conflicto de horario |
| HU-04 | Notificar estado de la solicitud |
| HU-05 | Consultar el estado de mis préstamos |
| HU-06 | Administrar laboratorios y equipos |
| HU-07 | Aprobar o rechazar solicitudes |
| HU-08 | Registrar la devolución de un recurso |
| HU-09 | Iniciar sesión de forma segura |
| HU-10 | Registrar y administrar usuarios |

## HU-01 — Consultar disponibilidad de recursos

- **ID / Épica:** HU-01 / Gestión de préstamos
- **Trazabilidad:** RU-01 / RF-01
- **Historia:** Como estudiante o docente, quiero consultar qué laboratorios y equipos están disponibles en una fecha y horario específico, para no tener que preguntarle al encargado cada vez que necesito un recurso.
- **Contexto / Escenario:** Panel principal del estudiante o docente, con sesión iniciada.
- **Valor de negocio / Outcome esperado:** Reducir la dependencia del encargado y el tiempo de búsqueda de recursos para las prácticas.
- **Alcance (incluye) / Out-of-scope:** Incluye filtro por fecha y horario y listado de recursos libres. No incluye la confirmación de la reserva.
- **Reglas de negocio:** RN-04

**Criterios de aceptación (Gherkin):**

```gherkin
Escenario 1: Hay recursos disponibles en el horario consultado
  Dado que he iniciado sesión como estudiante o docente
  Cuando selecciono una fecha y un horario y aplico el filtro de disponibilidad
  Entonces el sistema muestra únicamente los laboratorios y equipos sin reserva aprobada en ese rango

Escenario 2: No hay recursos disponibles en el horario consultado
  Dado que he iniciado sesión como estudiante o docente
  Cuando selecciono una fecha y un horario en el que todos los recursos ya están reservados
  Entonces el sistema muestra la lista vacía
  Y muestra un mensaje indicando que no hay recursos disponibles

Escenario 3: Recurso prestado sin devolución registrada
  Dado que un equipo fue prestado y el encargado aún no ha registrado su devolución
  Cuando consulto la disponibilidad para una fecha y un horario posteriores al préstamo
  Entonces ese equipo no aparece en la lista de recursos disponibles
```

- **NFR:** RNF-03
- **Supuestos / Dependencias / Riesgos:** El inventario base de laboratorios y equipos está cargado en el sistema (HU-06).
- **Datos de prueba:** Cuenta de estudiante activa; una fecha con recursos libres y otra con todos los recursos reservados.
- **DoR:** Historia redactada en formato Como / Quiero / Para, escenarios Gherkin definidos, dependencias identificadas y pantalla del prototipo asociada.
- **DoD:** Todos los escenarios se cumplen al probar la historia, se respetan las reglas de negocio y los RNF asociados, y el equipo valida el resultado.

## HU-02 — Solicitar préstamo de un recurso

- **ID / Épica:** HU-02 / Gestión de préstamos
- **Trazabilidad:** RU-02 / RF-02
- **Historia:** Como estudiante o docente, quiero registrar una solicitud de préstamo indicando el recurso, la fecha y el horario, para tener la seguridad de que el recurso estará apartado cuando lo necesite.
- **Contexto / Escenario:** Formulario de solicitud, al que se llega tras seleccionar un recurso disponible.
- **Valor de negocio / Outcome esperado:** Formalizar y dejar trazabilidad de los préstamos de forma digital, eliminando la planilla en papel.
- **Alcance (incluye) / Out-of-scope:** Incluye selección del recurso, diligenciamiento de datos y envío con estado "Pendiente". No incluye aprobación automática.
- **Reglas de negocio:** RN-03

**Criterios de aceptación (Gherkin):**

```gherkin
Escenario 1: Solicitud registrada correctamente
  Dado que he iniciado sesión y consulté un recurso disponible
  Cuando completo el formulario con recurso, fecha, horario y motivo, y confirmo el envío
  Entonces el sistema guarda la solicitud con estado "Pendiente"
  Y me muestra un mensaje de confirmación

Escenario 2: Solicitud con campos obligatorios vacíos
  Dado que estoy diligenciando el formulario de solicitud
  Cuando dejo un campo obligatorio vacío e intento enviarla
  Entonces el sistema no registra la solicitud
  Y me indica cuáles campos faltan por completar

Escenario 3: Solicitud con horario no válido
  Dado que estoy diligenciando el formulario de solicitud
  Cuando indico una fecha pasada o una hora de fin anterior o igual a la hora de inicio
  Entonces el sistema no registra la solicitud
  Y me indica que el horario no es válido
```

- **NFR:** RNF-01
- **Supuestos / Dependencias / Riesgos:** El recurso fue consultado previamente como disponible (HU-01).
- **Datos de prueba:** Envío de formulario completo; envío con campos obligatorios vacíos.
- **DoR:** Historia redactada en formato Como / Quiero / Para, escenarios Gherkin definidos, dependencias identificadas y pantalla del prototipo asociada.
- **DoD:** Todos los escenarios se cumplen al probar la historia, se respetan las reglas de negocio y los RNF asociados, y el equipo valida el resultado.

## HU-03 — Bloquear solicitudes en conflicto de horario

- **ID / Épica:** HU-03 / Gestión de préstamos
- **Trazabilidad:** RU-03 / RF-03
- **Historia:** Como estudiante o docente, quiero que el sistema impida reservar un recurso ya ocupado en el mismo horario, para no llegar al laboratorio y encontrar que alguien más también lo reservó.
- **Contexto / Escenario:** Envío de una solicitud de préstamo.
- **Valor de negocio / Outcome esperado:** Eliminar los conflictos y atrasos causados por reservas duplicadas.
- **Alcance (incluye) / Out-of-scope:** Incluye la validación de cruces de horario al registrar la solicitud y el aviso al solicitante. No incluye la sugerencia de horarios alternativos.
- **Reglas de negocio:** RN-01, RN-05

**Criterios de aceptación (Gherkin):**

```gherkin
Escenario 1: El recurso ya tiene una reserva aprobada en ese horario
  Dado que un recurso ya tiene una solicitud aprobada para un horario específico
  Cuando intento registrar una solicitud para el mismo recurso en un horario que se cruza
  Entonces el sistema bloquea el registro
  Y muestra un aviso de que el recurso no está disponible en ese horario

Escenario 2: Dos solicitudes simultáneas por el mismo recurso y horario
  Dado que dos usuarios envían al mismo tiempo una solicitud por el mismo recurso y horario
  Cuando el sistema procesa ambas solicitudes
  Entonces solo una queda registrada
  Y la otra es rechazada con un aviso de conflicto de horario

Escenario 3: El mismo usuario ya tiene una reserva en ese horario
  Dado que tengo una reserva aprobada para una fecha y hora
  Cuando intento registrar otra solicitud en un horario que se cruza con ella
  Entonces el sistema bloquea el registro
  Y me informa que ya tengo una reserva en ese horario
```

- **NFR:** RNF-05
- **Supuestos / Dependencias / Riesgos:** Existen solicitudes aprobadas contra las cuales validar el cruce (HU-07).
- **Datos de prueba:** Un recurso con reserva aprobada de 8:00 a 10:00; una nueva solicitud de 9:00 a 11:00; dos solicitudes enviadas al mismo tiempo.
- **DoR:** Historia redactada en formato Como / Quiero / Para, escenarios Gherkin definidos, dependencias identificadas y pantalla del prototipo asociada.
- **DoD:** Todos los escenarios se cumplen al probar la historia, se respetan las reglas de negocio y los RNF asociados, y el equipo valida el resultado.
