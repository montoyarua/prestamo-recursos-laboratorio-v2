# Reporte de Ejecución de Pruebas — MVP (Entregable 3)

**Proyecto:** Sistema de Préstamo de Recursos de Laboratorio (`prestamo-recursos-laboratorio-v2`)  
**Asignatura:** Diseño de Sistemas de Información · ITM  
**Ejecutado por:** Brayan Mejia  
**Fecha de ejecución:** 18 de noviembre de 2026  

---

## 1. Resumen Ejecutivo

Se ejecutó la batería completa de pruebas automatizadas del sistema mediante el script `./pruebas.sh`. El **100 % de las comprobaciones (142 de 142)** finalizaron exitosamente sin fallos, validando las 10 historias de usuario (`HU-01` a `HU-10`), los escenarios alternos, los cinco patrones de diseño GoF implementados (`State`, `Chain of Responsibility`, `Proxy`, `Observer` y `Factory Method`), las reglas de negocio transversales (`RN-01` a `RN-07`) y los requisitos no funcionales medibles (`RNF-03`, `RNF-05` y `RNF-06`).

---

## 2. Entorno de Ejecución

| Componente | Especificación utilizada |
| :--- | :--- |
| **Sistema Operativo** | Windows (64 bits) — Terminal Git Bash (`MINGW64`) |
| **Java Development Kit (JDK)** | Java 23.0.1 (`Java HotSpot(TM) 64-Bit Server VM, build 23.0.1+11-39`) |
| **Gestor de Construcción** | Apache Maven 3.9.9 |
| **Base de Datos de Referencia** | PostgreSQL 16 (contenedor Docker `prestamo-recursos-laboratorio-v2-postgres-1`) |
| **Comando de Ejecución** | `./pruebas.sh` |

---

## 3. Resultados Consolidados por Suite

| Suite de Pruebas | Clase / Alcance | Ejecutadas | Correctas | Fallidas | Estado |
| :--- | :--- | :---: | :---: | :---: | :---: |
| **Pruebas de Flujo y Dominio** | `PruebasFlujo` (reglas de negocio, estados, patrones GoF y escenarios Gherkin con tablas en memoria) | 99 | 99 | 0 | Aprobado |
| **Pruebas de Contrato REST** | `PruebasApi` (autenticación por token, códigos HTTP 200/400/401/403/409 y estructura JSON) | 43 | 43 | 0 | Aprobado |
| **Total General** | **Batería completa (`./pruebas.sh`)** | **142** | **142** | **0** | **100 % OK** |

---

## 4. Desglose Detallado por Historia de Usuario y Reglas

### 4.1 Suite `PruebasFlujo` (99 comprobaciones)

| Módulo / Historia de Usuario | Comprobaciones | Cobertura Funcional y Técnica Validada |
| :--- | :---: | :--- |
| **HU-09: Iniciar sesión de forma segura** | 8 | Acceso por rol (`Estudiante`, `Docente`, `Encargado`), rechazo ante contraseña errónea o usuario inexistente, validación de campos obligatorios, persistencia segura de contraseñas sin texto claro (`RNF-06`) y cierre de sesión. |
| **HU-10: Registrar y administrar usuarios** | 6 | Creación de cuentas con rol asignado, inicio de sesión inmediato, bloqueo de correos duplicados (`RN-07`), validación de rol obligatorio y control de acceso exclusivo para el encargado (`RN-02`). |
| **HU-06: Administrar laboratorios y equipos** | 10 | Creación en estado `Disponible` mediante patrón **Factory Method**, edición de datos, validación de campos obligatorios, bloqueo de eliminación con trámites vigentes (`RN-06`) y baja lógica preservando historial. |
| **HU-01: Consultar disponibilidad de recursos** | 10 | Filtrado por rango horario sin cruces aprobados, visibilidad de recursos con solicitudes pendientes (`RN-01`), bloqueo de equipos prestados sin devolución (`RN-04`) y validación de rangos inválidos (`fin <= inicio`). |
| **HU-02: Solicitar préstamo de un recurso** | 11 | Registro inicial en estado `Pendiente` (`RN-03`) con marca de tiempo, validación de fechas u horas pasadas, rechazo de campos vacíos y restricción para el rol encargado (`RN-02`). |
| **HU-03: Bloquear solicitudes en conflicto de horario** | 12 | Bloqueo ante reservas aprobadas cruzadas (`RN-01`), coexistencia de múltiples pendientes, prueba de concurrencia con 30 rondas de aprobación simultánea (`RNF-05`) y prevención de doble reserva simultánea del mismo usuario (`RN-05`). |
| **HU-07: Aprobar o rechazar solicitudes** | 17 | Orden FIFO de pendientes, restricción de permisos mediante patrón **Proxy** (`RN-02`), transiciones válidas con patrón **State**, generación de préstamo `Activo` y notificación automática mediante patrón **Observer**, y bloqueo de aprobaciones vencidas o cruzadas. |
| **HU-04: Notificar estado de la solicitud** | 5 | Visualización de avisos de aprobación o rechazo al iniciar sesión, aislamiento de notificaciones por usuario y marcado de lectura. |
| **HU-08: Registrar la devolución de un recurso** | 11 | Transición del préstamo a `Finalizado` y liberación del recurso a `Disponible`, bloqueo de doble devolución, control de rol mediante **Proxy** (`RN-02`) y liberación de reservas posteriores tras devolución (`RN-04`). |
| **HU-05: Consultar el estado de mis préstamos** | 4 | Consulta individual de préstamos `Activos` y `Finalizados` por solicitante y manejo de listas vacías. |
| **Reglas de negocio transversales** | 4 | Estado inicial `Pendiente` (`RN-03`), unicidad de correo insensible a mayúsculas (`RN-07`), habilitación de baja tras devolución (`RN-06`) y liberación completa tras retorno (`RN-01` / `RN-05`). |
| **Requisitos no funcionales medibles** | 1 | Rendimiento de consulta de disponibilidad con **504 recursos y 300 reservas** ejecutado en **2 ms** (umbral máximo exigido: `< 3000 ms`, `RNF-03`). |

### 4.2 Suite `PruebasApi` (43 comprobaciones)

| Grupo de Endpoints | Comprobaciones | Validaciones de Contrato HTTP |
| :--- | :---: | :--- |
| **API · HU-09 Sesión** | 6 | Rechazo sin token (`401`), credenciales inválidas (`401`), campos vacíos (`400`), emisión de token válido, ocultamiento de contraseñas en JSON e invalidación al cerrar sesión. |
| **API · HU-01 / HU-02 / HU-03** | 7 | Consulta de disponibilidad, registro en `Pendiente`, validación de fechas/horarios (`400`) y restricción de solicitud para encargado (`403`). |
| **API · HU-07 / HU-04 / RN-02** | 10 | Denegación de aprobación para estudiantes (`403`), aprobación y rechazo por encargado, bloqueo de conflictos de horario o transición duplicada (`409`) y consulta de notificaciones por rol. |
| **API · HU-05 / HU-08** | 8 | Listado de préstamos propios, restricción de devolución para estudiantes (`403`), registro de devolución (`Finalizado`) y rechazo de devolución repetida (`409`). |
| **API · HU-06 / HU-10** | 12 | CRUD de inventario y gestión de usuarios restringidos al encargado (`403` a otros roles), conflicto por trámites vigentes o correo duplicado (`409`) y exclusión de contraseñas en listados. |

---

## 5. Reproducibilidad de las Pruebas

Para volver a ejecutar la batería de pruebas en Windows (desde **Git Bash**) o Linux/macOS:

```bash
# 1. Ejecutar las 142 comprobaciones (dominio + contrato REST) usando solo JDK:
./pruebas.sh

# 2. (Opcional) Ejecutar las 99 pruebas de flujo directamente contra PostgreSQL real:
PRUEBAS_BD=postgres ./pruebas.sh
```
