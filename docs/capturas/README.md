# Índice de Capturas de Pantalla — MVP (Entregable 3)

Evidencia visual de la ejecución del **Sistema de Préstamo de Recursos de Laboratorio** (`prestamo-recursos-laboratorio-v2`), organizada por flujo funcional y trazada contra las historias de usuario (`HU-01` a `HU-10`) definidas en [../historias.md](../historias.md) y la matriz de verificación de [../trazabilidad.md](../trazabilidad.md).

---

## 1. Catálogo General de Capturas

| # | Archivo | Historia / Escenario | Figura Doc. Final | Descripción de la Evidencia |
| :-: | :--- | :--- | :---: | :--- |
| 1 | [01_login.png](01_login.png) | HU-09 · Esc. 1 | Figura 41 | Pantalla de inicio de sesión con credenciales válidas y redirección según el rol del usuario. |
| 2 | [02_login_error.png](02_login_error.png) | HU-09 · Esc. 2 | Figura 51 | Mensaje de error y denegación de acceso al ingresar usuario o contraseña incorrectos. |
| 3 | [03_login_vacio.png](03_login_vacio.png) | HU-09 · Esc. 3 | Figura 52 | Validación de campos obligatorios cuando se intenta iniciar sesión con datos vacíos. |
| 4 | [04_disponibilidad_panel.png](04_disponibilidad_panel.png) | HU-01 · Esc. 1 | — | Panel de consulta de disponibilidad con selección de fecha y rango horario. |
| 5 | [05_disponibles.png](05_disponibles.png) | HU-01 · Esc. 1 | Figura 42 | Listado de laboratorios y equipos libres sin reserva aprobada en el horario filtrado. |
| 6 | [06_solicitud_ok.png](06_solicitud_ok.png) | HU-02 · Esc. 1 | Figura 43 | Registro exitoso de solicitud de préstamo en estado `Pendiente` con confirmación visual. |
| 7 | [07_campos_vacios.png](07_campos_vacios.png) | HU-02 · Esc. 2 | Figura 54 | Bloqueo del formulario de solicitud y aviso de campos obligatorios sin diligenciar. |
| 8 | [08_fecha_pasada.png](08_fecha_pasada.png) | HU-02 · Esc. 3 | Figura 55 | Rechazo de solicitud al seleccionar una fecha anterior al día actual. |
| 9 | [09_fin_antes_inicio.png](09_fin_antes_inicio.png) | HU-02 · Esc. 3 | Figura 56 | Validación de horario inválido cuando la hora de fin es anterior o igual a la de inicio. |
| 10 | [10_docente_simultanea.png](10_docente_simultanea.png) | HU-03 · Esc. 2 | — | Registro simultáneo de solicitud por parte de un docente para el mismo recurso y horario en estado `Pendiente`. |
| 11 | [11_pendientes.png](11_pendientes.png) | HU-07 · General | — | Bandeja de solicitudes pendientes en el panel del encargado ordenadas por llegada. |
| 12 | [12_aprobada_1.png](12_aprobada_1.png) | HU-07 · Esc. 1 | Figura 45 | Aprobación de solicitud pendiente por el encargado (`Aprobada`) y creación del préstamo `Activo`. |
| 13 | [13_aprobar_cruzada_bloqueada.png](13_aprobar_cruzada_bloqueada.png) | HU-07 · Esc. 3 / HU-03 · Esc. 2 | Figura 59 | Bloqueo al intentar aprobar una solicitud cuyo horario se cruza con otra ya aprobada. |
| 14 | [14_rechazada.png](14_rechazada.png) | HU-07 · Esc. 2 | Figura 60 | Rechazo de solicitud pendiente por el encargado cambiando su estado a `Rechazada`. |
| 15 | [14b_hu03_esc1_recurso_reservado.png](14b_hu03_esc1_recurso_reservado.png) | HU-03 · Esc. 1 | Figura 57 | Bloqueo al solicitar un recurso que ya cuenta con una reserva aprobada en ese horario. |
| 16 | [14c_hu03_esc3_mismo_usuario.png](14c_hu03_esc3_mismo_usuario.png) | HU-03 · Esc. 3 | Figura 58 | Bloqueo cuando el mismo usuario intenta solicitar otro recurso en un horario donde ya tiene reserva aprobada. |
| 17 | [15_notificaciones_estudiante.png](15_notificaciones_estudiante.png) | HU-04 · Esc. 1 | Figura 46 | Notificación recibida por el estudiante informando que su solicitud fue aprobada. |
| 18 | [16_notificaciones_docente.png](16_notificaciones_docente.png) | HU-04 · Esc. 2 | Figura 60 | Notificación recibida por el docente informando que su solicitud fue rechazada. |
| 19 | [17_mis_prestamos_activo.png](17_mis_prestamos_activo.png) | HU-05 · Esc. 1 | Figura 47 | Vista «Mis préstamos» mostrando el préstamo del solicitante en estado `Activo`. |
| 20 | [18_mis_prestamos_vacio.png](18_mis_prestamos_vacio.png) | HU-05 · Esc. 2 | Figura 61 | Mensaje informativo en «Mis préstamos» cuando el usuario no tiene préstamos registrados. |
| 21 | [19_disponibles_sin_lab_redes.png](19_disponibles_sin_lab_redes.png) | HU-01 · Esc. 3 | Figura 53 | Exclusión en disponibilidad de un recurso prestado cuya devolución aún no se ha registrado. |
| 22 | [20_prestamos_activos.png](20_prestamos_activos.png) | HU-08 · General | — | Panel del encargado con el listado de préstamos activos pendientes de devolución. |
| 23 | [21_devolucion.png](21_devolucion.png) | HU-08 · Esc. 1 | Figura 48 | Registro de devolución del recurso por el encargado, liberando el recurso a `Disponible`. |
| 24 | [22_prestamo_finalizado.png](22_prestamo_finalizado.png) | HU-05 · Esc. 3 | — | Préstamo actualizado a estado `Finalizado` tras completarse su devolución. |
| 25 | [23_inventario.png](23_inventario.png) | HU-06 · General | — | Panel de gestión de inventario de laboratorios y equipos en la vista del encargado. |
| 26 | [24_recurso_registrado.png](24_recurso_registrado.png) | HU-06 · Esc. 1 | Figura 49 | Creación exitosa de un nuevo recurso en el inventario con estado inicial `Disponible`. |
| 27 | [25_eliminar_con_vigentes.png](25_eliminar_con_vigentes.png) | HU-06 · Esc. 4 | Figura 62 | Bloqueo al intentar eliminar un recurso que posee préstamos activos o solicitudes vigentes. |
| 28 | [26_usuario_creado.png](26_usuario_creado.png) | HU-10 · Esc. 1 | Figura 50 | Registro exitoso de una nueva cuenta de usuario con su rol asignado por el encargado. |
| 29 | [27_correo_duplicado.png](27_correo_duplicado.png) | HU-10 · Esc. 2 | Figura 63 | Rechazo al registrar un usuario con un correo electrónico ya existente en el sistema. |
| 30 | [28_usuario_sin_rol.png](28_usuario_sin_rol.png) | HU-10 · Esc. 3 | Figura 64 | Validación de campos obligatorios y selección de rol en el registro de usuarios. |
| 31 | [contacto.png](contacto.png) | Vista general | — | Sección informativa de contacto y soporte del sistema de préstamo de laboratorios. |

---

## 2. Agrupación por Rol de Usuario

* **Acceso y Autenticación (`HU-09`):** `01_login.png`, `02_login_error.png`, `03_login_vacio.png`.
* **Flujo del Solicitante — Estudiante y Docente (`HU-01` a `HU-05`):** `04_disponibilidad_panel.png`, `05_disponibles.png`, `06_solicitud_ok.png`, `07_campos_vacios.png`, `08_fecha_pasada.png`, `09_fin_antes_inicio.png`, `10_docente_simultanea.png`, `14b_hu03_esc1_recurso_reservado.png`, `14c_hu03_esc3_mismo_usuario.png`, `15_notificaciones_estudiante.png`, `16_notificaciones_docente.png`, `17_mis_prestamos_activo.png`, `18_mis_prestamos_vacio.png`, `19_disponibles_sin_lab_redes.png`, `22_prestamo_finalizado.png`.
* **Flujo del Encargado de Laboratorio (`HU-06`, `HU-07`, `HU-08`, `HU-10`):** `11_pendientes.png`, `12_aprobada_1.png`, `13_aprobar_cruzada_bloqueada.png`, `14_rechazada.png`, `20_prestamos_activos.png`, `21_devolucion.png`, `23_inventario.png`, `24_recurso_registrado.png`, `25_eliminar_con_vigentes.png`, `26_usuario_creado.png`, `27_correo_duplicado.png`, `28_usuario_sin_rol.png`.
