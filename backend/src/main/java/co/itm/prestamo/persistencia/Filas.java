package co.itm.prestamo.persistencia;

import java.time.*;

/** Filas de las cinco tablas del modelo de datos (ver docs/base_de_datos.sql). */
public final class Filas {
    private Filas() { }

    public record UsuarioFila(long id, String nombre, String correo, String contrasena, String rol) implements Fila<UsuarioFila> {
        public UsuarioFila conId(long n) { return new UsuarioFila(n, nombre, correo, contrasena, rol); }
    }
    /** {@code activo = false} es la baja lógica de un recurso eliminado (conserva el historial de sus solicitudes). */
    public record RecursoFila(long id, String tipo, String nombre, String estado, String ubicacion, Integer capacidad,
                              String marca, String modelo, String numeroSerie, boolean activo) implements Fila<RecursoFila> {
        public RecursoFila conId(long n) { return new RecursoFila(n, tipo, nombre, estado, ubicacion, capacidad, marca, modelo, numeroSerie, activo); }
    }
    public record SolicitudFila(long id, long usuarioId, long recursoId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
                                String motivo, String estado, LocalDateTime fechaRegistro) implements Fila<SolicitudFila> {
        public SolicitudFila conId(long n) { return new SolicitudFila(n, usuarioId, recursoId, fecha, horaInicio, horaFin, motivo, estado, fechaRegistro); }
    }
    public record PrestamoFila(long id, long solicitudId, String estado, LocalDateTime fechaCreacion, LocalDateTime fechaDevolucion) implements Fila<PrestamoFila> {
        public PrestamoFila conId(long n) { return new PrestamoFila(n, solicitudId, estado, fechaCreacion, fechaDevolucion); }
    }
    public record NotificacionFila(long id, long usuarioId, String mensaje, LocalDateTime fechaCreacion, boolean leida) implements Fila<NotificacionFila> {
        public NotificacionFila conId(long n) { return new NotificacionFila(n, usuarioId, mensaje, fechaCreacion, leida); }
    }
}
