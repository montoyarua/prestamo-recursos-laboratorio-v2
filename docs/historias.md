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

## HU-04 — Notificar estado de la solicitud

- **ID / Épica:** HU-04 / Seguimiento y control
- **Trazabilidad:** RU-04 / RF-04
- **Historia:** Como estudiante o docente, quiero que me avisen claramente si mi solicitud fue aprobada o rechazada, para saber si puedo contar con el recurso o debo buscar otra opción.
- **Contexto / Escenario:** Panel principal del estudiante o docente, al iniciar sesión.
- **Valor de negocio / Outcome esperado:** Reducir la incertidumbre del solicitante y evitar desplazamientos innecesarios al laboratorio.
- **Alcance (incluye) / Out-of-scope:** Incluye avisos dentro del sistema al iniciar sesión. No incluye notificaciones al celular ni correos electrónicos.
- **Reglas de negocio:** RN-03

**Criterios de aceptación (Gherkin):**

```gherkin
Escenario 1: Solicitud aprobada
  Dado que el encargado aprobó una de mis solicitudes pendientes
  Cuando inicio sesión en el sistema
  Entonces veo una notificación indicando que mi solicitud fue aprobada

Escenario 2: Solicitud rechazada
  Dado que el encargado rechazó una de mis solicitudes pendientes
  Cuando inicio sesión en el sistema
  Entonces veo una notificación indicando que mi solicitud fue rechazada

Escenario 3: Solicitud aún sin respuesta
  Dado que mi solicitud sigue en estado "Pendiente" porque el encargado no la ha revisado
  Cuando inicio sesión en el sistema
  Entonces no veo ninguna notificación de aprobación ni de rechazo
  Y la solicitud sigue apareciendo como "Pendiente"
```

- **NFR:** Global (RNF-02, RNF-04)
- **Supuestos / Dependencias / Riesgos:** El encargado ya gestionó la solicitud (HU-07).
- **Datos de prueba:** Una solicitud aprobada y una rechazada, ambas del mismo usuario.
- **DoR:** Historia redactada en formato Como / Quiero / Para, escenarios Gherkin definidos, dependencias identificadas y pantalla del prototipo asociada.
- **DoD:** Todos los escenarios se cumplen al probar la historia, se respetan las reglas de negocio y los RNF asociados, y el equipo valida el resultado.

## HU-05 — Consultar el estado de mis préstamos

- **ID / Épica:** HU-05 / Seguimiento y control
- **Trazabilidad:** RU-05 / RF-05
- **Historia:** Como estudiante o docente, quiero revisar qué préstamos tengo activos y cuáles ya devolví, para tener control de lo que me falta entregar.
- **Contexto / Escenario:** Sección "Mis préstamos" del panel del estudiante o docente, con sesión iniciada.
- **Valor de negocio / Outcome esperado:** Dar autonomía al usuario para hacer seguimiento de sus recursos sin depender del encargado.
- **Alcance (incluye) / Out-of-scope:** Incluye el listado de préstamos activos y finalizados con su estado. No incluye editar ni cancelar préstamos desde esta pantalla.
- **Reglas de negocio:** RN-04

**Criterios de aceptación (Gherkin):**

```gherkin
Escenario 1: Usuario con préstamos activos y finalizados
  Dado que tengo al menos un préstamo activo y uno finalizado
  Cuando ingreso a la sección "Mis préstamos"
  Entonces veo ambos en la lista
  Y cada uno aparece marcado con su estado

Escenario 2: Usuario sin préstamos
  Dado que nunca he solicitado un préstamo
  Cuando ingreso a la sección "Mis préstamos"
  Entonces el sistema muestra un mensaje indicando que no tengo préstamos registrados

Escenario 3: Préstamo devuelto pasa a finalizado
  Dado que tengo un préstamo activo y el encargado registra su devolución
  Cuando ingreso a la sección "Mis préstamos"
  Entonces ese préstamo aparece con estado "Finalizado"
```

- **NFR:** Global (RNF-02, RNF-04)
- **Supuestos / Dependencias / Riesgos:** Las solicitudes y devoluciones se registraron correctamente (HU-07, HU-08).
- **Datos de prueba:** Usuario con préstamos activos y finalizados; usuario sin ningún préstamo.
- **DoR:** Historia redactada en formato Como / Quiero / Para, escenarios Gherkin definidos, dependencias identificadas y pantalla del prototipo asociada.
- **DoD:** Todos los escenarios se cumplen al probar la historia, se respetan las reglas de negocio y los RNF asociados, y el equipo valida el resultado.

## HU-06 — Administrar laboratorios y equipos

- **ID / Épica:** HU-06 / Gestión de inventario
- **Trazabilidad:** RU-06 / RF-06
- **Historia:** Como encargado de laboratorio, quiero registrar, editar, eliminar y consultar los laboratorios y equipos, para mantener el inventario siempre actualizado.
- **Contexto / Escenario:** Panel de inventario, con sesión iniciada como encargado.
- **Valor de negocio / Outcome esperado:** Garantizar que la disponibilidad que ven los usuarios sea precisa.
- **Alcance (incluye) / Out-of-scope:** Incluye registrar, modificar, eliminar y consultar laboratorios y equipos. No incluye la gestión de mantenimiento de los recursos.
- **Reglas de negocio:** RN-06

**Criterios de aceptación (Gherkin):**

```gherkin
Escenario 1: Registrar un nuevo recurso
  Dado que he iniciado sesión como encargado
  Cuando completo el formulario de un nuevo laboratorio o equipo y lo guardo
  Entonces el recurso queda visible en el inventario con estado "Disponible"

Escenario 2: Modificar un recurso existente
  Dado que existe un recurso registrado en el inventario
  Cuando modifico sus datos y guardo los cambios
  Entonces el sistema muestra la información actualizada al consultarlo

Escenario 3: Eliminar un recurso sin préstamos ni solicitudes
  Dado que un recurso no tiene préstamos activos ni solicitudes pendientes o aprobadas
  Cuando lo elimino desde el inventario
  Entonces el recurso deja de aparecer en la consulta de disponibilidad

Escenario 4: Intentar eliminar un recurso con préstamos o solicitudes vigentes
  Dado que un recurso tiene un préstamo activo o una solicitud pendiente o aprobada
  Cuando intento eliminarlo desde el inventario
  Entonces el sistema no lo elimina
  Y me informa que el recurso tiene préstamos o solicitudes vigentes
```

- **NFR:** RNF-02
- **Supuestos / Dependencias / Riesgos:** El encargado tiene una cuenta con su rol asignado (HU-09, HU-10).
- **Datos de prueba:** Crear, editar y eliminar un laboratorio de prueba; intentar eliminar un equipo con un préstamo activo.
- **DoR:** Historia redactada en formato Como / Quiero / Para, escenarios Gherkin definidos, dependencias identificadas y pantalla del prototipo asociada.
- **DoD:** Todos los escenarios se cumplen al probar la historia, se respetan las reglas de negocio y los RNF asociados, y el equipo valida el resultado.

## HU-07 — Aprobar o rechazar solicitudes

- **ID / Épica:** HU-07 / Gestión de préstamos
- **Trazabilidad:** RU-07 / RF-07
- **Historia:** Como encargado de laboratorio, quiero aprobar o rechazar las solicitudes pendientes desde un solo lugar, para tener claridad de quién usará cada recurso.
- **Contexto / Escenario:** Panel de solicitudes pendientes del encargado.
- **Valor de negocio / Outcome esperado:** Centralizar la decisión y evitar aprobaciones informales o duplicadas.
- **Alcance (incluye) / Out-of-scope:** Incluye el cambio de estado de la solicitud (aprobar o rechazar). No incluye editar el contenido de la solicitud.
- **Reglas de negocio:** RN-01, RN-02, RN-03

**Criterios de aceptación (Gherkin):**

```gherkin
Escenario 1: Aprobar una solicitud pendiente
  Dado que existe una solicitud con estado "Pendiente"
  Cuando la reviso y la apruebo
  Entonces su estado cambia a "Aprobada"
  Y el solicitante recibe la notificación correspondiente

Escenario 2: Rechazar una solicitud pendiente
  Dado que existe una solicitud con estado "Pendiente"
  Cuando la reviso y la rechazo
  Entonces su estado cambia a "Rechazada"
  Y el solicitante recibe la notificación correspondiente

Escenario 3: Aprobar una solicitud que se cruza con otra ya aprobada
  Dado que hay dos solicitudes pendientes para el mismo recurso en horarios que se cruzan y ya aprobé una de ellas
  Cuando intento aprobar la otra
  Entonces el sistema no permite la aprobación
  Y me informa que el recurso ya está reservado en ese horario
```

- **NFR:** RNF-02
- **Supuestos / Dependencias / Riesgos:** Existen solicitudes en estado "Pendiente" (HU02).
- **Datos de prueba:** Dos solicitudes pendientes: una para aprobar y otra para rechazar.
- **DoR:** Historia redactada en formato Como / Quiero / Para, escenarios Gherkin definidos, dependencias identificadas y pantalla del prototipo asociada.
- **DoD:** Todos los escenarios se cumplen al probar la historia, se respetan las reglas de negocio y los RNF asociados, y el equipo valida el resultado.

## HU-08 — Registrar la devolución de un recurso

- **ID / Épica:** HU-08 / Gestión de préstamos
- **Trazabilidad:** RU-08 / RF-08
- **Historia:** Como encargado de laboratorio, quiero registrar cuándo se devuelve un equipo o se desocupa un laboratorio, para que vuelva a quedar disponible para otra persona.
- **Contexto / Escenario:** Panel de préstamos activos del encargado.
- **Valor de negocio / Outcome esperado:** Mantener actualizada la disponibilidad y evitar que un recurso quede bloqueado sin necesidad.
- **Alcance (incluye) / Out-of-scope:** Incluye marcar la devolución de un préstamo activo. No incluye evaluar el estado físico del recurso devuelto.
- **Reglas de negocio:** RN-02, RN-04

**Criterios de aceptación (Gherkin):**

```gherkin
Escenario 1: Registrar la devolución de un préstamo activo
  Dado que existe un préstamo con estado "Activo"
  Cuando registro su devolución
  Entonces el préstamo cambia a "Finalizado"
  Y el recurso vuelve a aparecer como "Disponible"

Escenario 2: Intentar devolver un préstamo ya finalizado
  Dado que un préstamo ya tiene estado "Finalizado"
  Cuando intento registrar su devolución nuevamente
  Entonces el sistema no permite la acción
  Y muestra un mensaje indicando que el recurso ya fue devuelto

Escenario 3: Usuario sin rol de encargado
  Dado que he iniciado sesión como estudiante o docente
  Cuando intento registrar la devolución de un préstamo
  Entonces el sistema deniega la acción
  Y muestra un mensaje indicando que mi rol no tiene permiso
```

- **NFR:** RNF-02
- **Supuestos / Dependencias / Riesgos:** El préstamo existe con estado "Activo", generado a partir de una solicitud aprobada (HU-07).
- **Datos de prueba:** Un préstamo activo para devolver; un préstamo ya finalizado.
- **DoR:** Historia redactada en formato Como / Quiero / Para, escenarios Gherkin definidos, dependencias identificadas y pantalla del prototipo asociada.
- **DoD:** Todos los escenarios se cumplen al probar la historia, se respetan las reglas de negocio y los RNF asociados, y el equipo valida el resultado.

## HU-09 — Iniciar sesión de forma segura

- **ID / Épica:** HU-09 / Gestión de usuarios
- **Trazabilidad:** RU-09 / RF-09
- **Historia:** Como usuario del sistema (estudiante, docente o encargado), quiero ingresar con mi usuario y contraseña, para ver solo las funciones que corresponden a mi rol.
- **Contexto / Escenario:** Pantalla de inicio de sesión, punto de entrada al sistema.
- **Valor de negocio / Outcome esperado:** Garantizar un acceso seguro y diferenciado según el rol de cada usuario.
- **Alcance (incluye) / Out-of-scope:** Incluye la validación de credenciales y el ingreso al panel del rol. No incluye la recuperación de contraseña.
- **Reglas de negocio:** N/A

**Criterios de aceptación (Gherkin):**

```gherkin
Escenario 1: Credenciales correctas
  Dado que tengo una cuenta activa en el sistema
  Cuando ingreso mi usuario y contraseña correctos
  Entonces accedo al sistema
  Y veo únicamente las funciones habilitadas para mi rol

Escenario 2: Credenciales incorrectas
  Dado que estoy en la pantalla de inicio de sesión
  Cuando escribo un usuario o una contraseña incorrectos
  Entonces el sistema deniega el acceso
  Y muestra un mensaje de error claro

Escenario 3: Campos vacíos
  Dado que estoy en la pantalla de inicio de sesión
  Cuando dejo el usuario o la contraseña sin diligenciar e intento ingresar
  Entonces el sistema no permite el ingreso
  Y me indica cuáles campos son obligatorios
```

- **NFR:** RNF-02
- **Supuestos / Dependencias / Riesgos:** El usuario fue registrado previamente (HU-10).
- **Datos de prueba:** Ingreso con credenciales correctas e incorrectas para cada uno de los tres roles.
- **DoR:** Historia redactada en formato Como / Quiero / Para, escenarios Gherkin definidos, dependencias identificadas y pantalla del prototipo asociada.
- **DoD:** Todos los escenarios se cumplen al probar la historia, se respetan las reglas de negocio y los RNF asociados, y el equipo valida el resultado.

## HU-10 — Registrar y administrar usuarios

- **ID / Épica:** HU-10 / Gestión de usuarios
- **Trazabilidad:** RU-10 / RF-10
- **Historia:** Como encargado de laboratorio, quiero registrar nuevas cuentas de estudiantes, docentes u otros encargados y asignarles un rol, para que cada persona tenga acceso a las funciones que le corresponden.
- **Contexto / Escenario:** Panel de usuarios del encargado.
- **Valor de negocio / Outcome esperado:** Controlar de forma centralizada quién accede al sistema y con qué permisos.
- **Alcance (incluye) / Out-of-scope:** Incluye la creación de la cuenta y la asignación del rol. No incluye editar ni eliminar usuarios existentes (propuesto para el Release 2).
- **Reglas de negocio:** RN-07

**Criterios de aceptación (Gherkin):**

```gherkin
Escenario 1: Crear una cuenta con rol asignado
  Dado que he iniciado sesión como encargado
  Cuando completo el formulario de un nuevo usuario y le asigno un rol
  Entonces la cuenta queda creada
  Y el nuevo usuario puede iniciar sesión con los permisos de ese rol

Escenario 2: Correo ya registrado
  Dado que ya existe una cuenta registrada con un correo específico
  Cuando intento crear un nuevo usuario con ese mismo correo
  Entonces el sistema rechaza el registro
  Y muestra un mensaje de correo duplicado

Escenario 3: Formulario incompleto o sin rol
  Dado que he iniciado sesión como encargado
  Cuando intento guardar un usuario nuevo sin asignarle un rol o con campos obligatorios vacíos
  Entonces el sistema no crea la cuenta
  Y me indica qué datos faltan
```

- **NFR:** RNF-02
- **Supuestos / Dependencias / Riesgos:** Existe al menos una cuenta de encargado para crear las demás.
- **Datos de prueba:** Registrar un usuario con cada uno de los tres roles; intentar registrar un correo repetido.
- **DoR:** Historia redactada en formato Como / Quiero / Para, escenarios Gherkin definidos, dependencias identificadas y pantalla del prototipo asociada.
- **DoD:** Todos los escenarios se cumplen al probar la historia, se respetan las reglas de negocio y los RNF asociados, y el equipo valida el resultado.
