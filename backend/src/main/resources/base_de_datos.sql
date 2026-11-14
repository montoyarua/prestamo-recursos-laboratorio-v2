-- Modelo de datos PostgreSQL del Sistema de Préstamo de Recursos de Laboratorio.
-- Se ejecuta al arrancar la aplicación (Configuracion.crearEsquema) y es idempotente.
CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TABLE IF NOT EXISTS usuario (
    id          BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(120) NOT NULL,
    correo      VARCHAR(160) NOT NULL,
    contrasena  VARCHAR(200) NOT NULL,                       -- resumen PBKDF2 con sal (RNF-06), nunca texto claro
    rol         VARCHAR(20)  NOT NULL CHECK (rol IN ('ESTUDIANTE', 'DOCENTE', 'ENCARGADO'))
);
CREATE UNIQUE INDEX IF NOT EXISTS ux_usuario_correo ON usuario (LOWER(correo));   -- RN-07

CREATE TABLE IF NOT EXISTS recurso (
    id            BIGSERIAL PRIMARY KEY,
    tipo          VARCHAR(20)  NOT NULL CHECK (tipo IN ('LABORATORIO', 'EQUIPO')),
    nombre        VARCHAR(120) NOT NULL,
    estado        VARCHAR(20)  NOT NULL CHECK (estado IN ('DISPONIBLE', 'PRESTADO')),
    ubicacion     VARCHAR(160),
    capacidad     INTEGER,
    marca         VARCHAR(80),
    modelo        VARCHAR(80),
    numero_serie  VARCHAR(80),
    activo        BOOLEAN NOT NULL DEFAULT TRUE,             -- FALSE = eliminado (baja lógica, conserva el historial)
    CHECK ((tipo = 'LABORATORIO' AND ubicacion IS NOT NULL AND capacidad IS NOT NULL)
        OR (tipo = 'EQUIPO' AND marca IS NOT NULL AND modelo IS NOT NULL AND numero_serie IS NOT NULL))
);

CREATE TABLE IF NOT EXISTS solicitud (
    id              BIGSERIAL PRIMARY KEY,
    usuario_id      BIGINT       NOT NULL REFERENCES usuario (id),
    recurso_id      BIGINT       NOT NULL REFERENCES recurso (id),
    fecha           DATE         NOT NULL,
    hora_inicio     TIME         NOT NULL,
    hora_fin        TIME         NOT NULL,
    motivo          VARCHAR(500),
    estado          VARCHAR(20)  NOT NULL CHECK (estado IN ('PENDIENTE', 'APROBADA', 'RECHAZADA')),
    fecha_registro  TIMESTAMP    NOT NULL,
    devuelta        BOOLEAN      NOT NULL DEFAULT FALSE,     -- la marca el préstamo al finalizar (trigger de abajo)
    CHECK (hora_inicio < hora_fin),
    -- RN-01 y RNF-05: dos reservas aprobadas y sin devolver del mismo recurso no pueden cruzarse, aunque lleguen a la vez.
    CONSTRAINT sin_cruce_por_recurso EXCLUDE USING gist (
        recurso_id WITH =,
        tsrange(fecha + hora_inicio, fecha + hora_fin) WITH &&
    ) WHERE (estado = 'APROBADA' AND NOT devuelta)
);
CREATE INDEX IF NOT EXISTS ix_solicitud_usuario ON solicitud (usuario_id);

CREATE TABLE IF NOT EXISTS prestamo (
    id                BIGSERIAL PRIMARY KEY,
    solicitud_id      BIGINT      NOT NULL UNIQUE REFERENCES solicitud (id),
    estado            VARCHAR(20) NOT NULL CHECK (estado IN ('ACTIVO', 'FINALIZADO')),
    fecha_creacion    TIMESTAMP   NOT NULL,
    fecha_devolucion  TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notificacion (
    id              BIGSERIAL PRIMARY KEY,
    usuario_id      BIGINT       NOT NULL REFERENCES usuario (id),
    mensaje         VARCHAR(500) NOT NULL,
    fecha_creacion  TIMESTAMP    NOT NULL,
    leida           BOOLEAN      NOT NULL DEFAULT FALSE
);
CREATE INDEX IF NOT EXISTS ix_notificacion_usuario ON notificacion (usuario_id);

-- Al finalizar un préstamo, su solicitud deja de bloquear al recurso (RN-04).
CREATE OR REPLACE FUNCTION liberar_solicitud() RETURNS trigger AS $$
BEGIN
    IF NEW.estado = 'FINALIZADO' THEN
        UPDATE solicitud SET devuelta = TRUE WHERE id = NEW.solicitud_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS tg_liberar_solicitud ON prestamo;
CREATE TRIGGER tg_liberar_solicitud AFTER INSERT OR UPDATE OF estado ON prestamo
    FOR EACH ROW EXECUTE FUNCTION liberar_solicitud();
