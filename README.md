# Sistema de Préstamo de Recursos de Laboratorio — MVP (Entregable 3)

Diseño de Sistemas de Información · ITM · Docente: Alexandra Guerrero Bocanegra

Aplicación web para consultar disponibilidad, solicitar, aprobar y devolver laboratorios y equipos. Implementa las 10 historias
de usuario (HU-01 a HU-10) con sus escenarios alternos y los cinco patrones GoF del Entregable 2:
**State, Chain of Responsibility, Proxy, Observer y Factory Method**.

## Tecnologías (las del modelo C4 del Entregable 2)
| Contenedor | Tecnología |
|---|---|
| Aplicación web | React 18 (SPA) compilada con Vite |
| Backend/API | Java 21 + Spring Boot 3.3 (Spring MVC, Spring JDBC, transacciones) |
| Base de datos | PostgreSQL 16 (esquema en `backend/src/main/resources/base_de_datos.sql`) |

## Requisitos
- JDK 21, Maven 3.9 o superior y PostgreSQL 14 o superior.
- Node.js 20 o superior **solo** si se quiere recompilar la interfaz (la versión compilada ya está en `backend/src/main/resources/static`).

## Ejecutar
1. Crear la base de datos (una vez). Con Docker: `docker compose up -d`. Sin Docker, en `psql` como administrador:
   ```sql
   CREATE USER prestamos WITH PASSWORD 'prestamos';
   CREATE DATABASE prestamos OWNER prestamos;
   ```
   La aplicación crea las tablas al arrancar. Usa la extensión `btree_gist` (viene con PostgreSQL); si el usuario no puede crearla,
   ejecutar una vez como administrador: `CREATE EXTENSION btree_gist;` dentro de la base `prestamos`.
2. Arrancar: `./ejecutar.sh` (equivale a `cd backend && mvn spring-boot:run`).
3. Abrir http://localhost:8080 e iniciar sesión con una cuenta de demostración (se cargan solo si la base está vacía):

| Rol        | Correo                   | Contraseña     |
|------------|--------------------------|----------------|
| Estudiante | estudiante@itm.edu.co    | Estudiante123  |
| Docente    | docente@itm.edu.co       | Docente123     |
| Encargado  | encargado@itm.edu.co     | Encargado123   |

Variables opcionales: `DB_URL`, `DB_USER`, `DB_PASSWORD`, `PORT`, `DATOS_DEMO=false`.

### Recompilar la interfaz React
```
cd frontend && npm install && npm run build     # deja la salida en backend/src/main/resources/static
npm run dev                                      # modo desarrollo en http://localhost:5173 (usa la API en :8080)
```

## Pruebas
```
./pruebas.sh                     # solo JDK: 99 comprobaciones de reglas (al menos una por escenario Gherkin) + 43 de contrato REST
PRUEBAS_BD=postgres ./pruebas.sh # las 99 de reglas contra un PostgreSQL real (necesita el cliente psql y las variables PGHOST, PGUSER, PGPASSWORD, PGDATABASE)
```
`pruebas.sh` no usa Spring ni PostgreSQL: ejecuta el dominio y los servicios con tablas en memoria (clases que viven solo en `src/test`; la aplicación real guarda siempre en PostgreSQL) y un servidor HTTP de contrato
(`backend/src/test/.../contrato`) que expone las mismas rutas que los controladores Spring. Con `PRUEBAS_BD=postgres` las mismas pruebas
usan las tablas reales del esquema (restricción de exclusión y disparador incluidos).

## Estructura
```
backend/                        Proyecto Maven (Spring Boot)
  src/main/java/co/itm/prestamo/
    dominio/          Usuario, Recurso (Laboratorio, Equipo), Solicitud + State, Prestamo, Notificacion
    usuarios/ inventario/ disponibilidad/ solicitudes/ prestamos/   Servicios, patrones y repositorios por componente
    puertos/          Interfaces entre componentes (AutorizacionPort, DisponibilidadPort, ...)
    persistencia/     Tabla, Filas, TablaMemoria; jdbc/ = tablas de PostgreSQL con Spring JDBC
    api/              Vistas y Convertir (formato JSON de las respuestas)
    spring/           Aplicacion, Configuracion, FiltroAutenticacion, controladores *Rest, transacciones, errores
    Main.java         Raíz de composición (la usa Spring y las pruebas)
  src/main/resources/ application.properties, base_de_datos.sql, static/ (interfaz compilada)
  src/test/java/      PruebasFlujo, PruebasApi, Verificador, contrato/ (servidor de contrato y tablas psql)
frontend/             React + Vite (src/vistas = una pantalla por historia de usuario)
docs/uml/             Diagramas UML en PlantUML (.puml) y PNG
docs/capturas/        Capturas del MVP: flujos principales y alternos
```
