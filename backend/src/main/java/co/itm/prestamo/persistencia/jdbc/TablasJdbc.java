package co.itm.prestamo.persistencia.jdbc;

import co.itm.prestamo.persistencia.Filas.*;
import co.itm.prestamo.persistencia.Tablas;
import java.sql.Types;
import java.time.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/** Las cinco tablas de PostgreSQL (el esquema está en docs/base_de_datos.sql). */
public final class TablasJdbc {
    private TablasJdbc() { }

    public static Tablas crear(JdbcTemplate jdbc) {
        return new Tablas(new Usuarios(jdbc), new Recursos(jdbc), new Solicitudes(jdbc), new Prestamos(jdbc), new Notificaciones(jdbc));
    }

    static final class Usuarios extends TablaJdbc<UsuarioFila> {
        Usuarios(JdbcTemplate j) { super(j); }
        @Override protected String tabla() { return "usuario"; }
        @Override protected String[] columnas() { return new String[] {"nombre", "correo", "contrasena", "rol"}; }
        @Override protected RowMapper<UsuarioFila> mapeo() {
            return (rs, n) -> new UsuarioFila(rs.getLong("id"), rs.getString("nombre"), rs.getString("correo"), rs.getString("contrasena"), rs.getString("rol"));
        }
        @Override protected Object[] valores(UsuarioFila f) { return new Object[] {f.nombre(), f.correo(), f.contrasena(), f.rol()}; }
        @Override protected int[] tipos() { return new int[] {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR}; }
    }

    static final class Recursos extends TablaJdbc<RecursoFila> {
        Recursos(JdbcTemplate j) { super(j); }
        @Override protected String tabla() { return "recurso"; }
        @Override protected String[] columnas() { return new String[] {"tipo", "nombre", "estado", "ubicacion", "capacidad", "marca", "modelo", "numero_serie", "activo"}; }
        @Override protected RowMapper<RecursoFila> mapeo() {
            return (rs, n) -> new RecursoFila(rs.getLong("id"), rs.getString("tipo"), rs.getString("nombre"), rs.getString("estado"),
                rs.getString("ubicacion"), rs.getObject("capacidad", Integer.class), rs.getString("marca"), rs.getString("modelo"),
                rs.getString("numero_serie"), rs.getBoolean("activo"));
        }
        @Override protected Object[] valores(RecursoFila f) {
            return new Object[] {f.tipo(), f.nombre(), f.estado(), f.ubicacion(), f.capacidad(), f.marca(), f.modelo(), f.numeroSerie(), f.activo()};
        }
        @Override protected int[] tipos() {
            return new int[] {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BOOLEAN};
        }
    }

    static final class Solicitudes extends TablaJdbc<SolicitudFila> {
        Solicitudes(JdbcTemplate j) { super(j); }
        @Override protected String tabla() { return "solicitud"; }
        @Override protected String[] columnas() { return new String[] {"usuario_id", "recurso_id", "fecha", "hora_inicio", "hora_fin", "motivo", "estado", "fecha_registro"}; }
        @Override protected RowMapper<SolicitudFila> mapeo() {
            return (rs, n) -> new SolicitudFila(rs.getLong("id"), rs.getLong("usuario_id"), rs.getLong("recurso_id"),
                rs.getObject("fecha", LocalDate.class), rs.getObject("hora_inicio", LocalTime.class), rs.getObject("hora_fin", LocalTime.class),
                rs.getString("motivo"), rs.getString("estado"), rs.getObject("fecha_registro", LocalDateTime.class));
        }
        @Override protected Object[] valores(SolicitudFila f) {
            return new Object[] {f.usuarioId(), f.recursoId(), f.fecha(), f.horaInicio(), f.horaFin(), f.motivo(), f.estado(), f.fechaRegistro()};
        }
        @Override protected int[] tipos() {
            return new int[] {Types.BIGINT, Types.BIGINT, Types.DATE, Types.TIME, Types.TIME, Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP};
        }
    }

    static final class Prestamos extends TablaJdbc<PrestamoFila> {
        Prestamos(JdbcTemplate j) { super(j); }
        @Override protected String tabla() { return "prestamo"; }
        @Override protected String[] columnas() { return new String[] {"solicitud_id", "estado", "fecha_creacion", "fecha_devolucion"}; }
        @Override protected RowMapper<PrestamoFila> mapeo() {
            return (rs, n) -> new PrestamoFila(rs.getLong("id"), rs.getLong("solicitud_id"), rs.getString("estado"),
                rs.getObject("fecha_creacion", LocalDateTime.class), rs.getObject("fecha_devolucion", LocalDateTime.class));
        }
        @Override protected Object[] valores(PrestamoFila f) { return new Object[] {f.solicitudId(), f.estado(), f.fechaCreacion(), f.fechaDevolucion()}; }
        @Override protected int[] tipos() { return new int[] {Types.BIGINT, Types.VARCHAR, Types.TIMESTAMP, Types.TIMESTAMP}; }
    }

    static final class Notificaciones extends TablaJdbc<NotificacionFila> {
        Notificaciones(JdbcTemplate j) { super(j); }
        @Override protected String tabla() { return "notificacion"; }
        @Override protected String[] columnas() { return new String[] {"usuario_id", "mensaje", "fecha_creacion", "leida"}; }
        @Override protected RowMapper<NotificacionFila> mapeo() {
            return (rs, n) -> new NotificacionFila(rs.getLong("id"), rs.getLong("usuario_id"), rs.getString("mensaje"),
                rs.getObject("fecha_creacion", LocalDateTime.class), rs.getBoolean("leida"));
        }
        @Override protected Object[] valores(NotificacionFila f) { return new Object[] {f.usuarioId(), f.mensaje(), f.fechaCreacion(), f.leida()}; }
        @Override protected int[] tipos() { return new int[] {Types.BIGINT, Types.VARCHAR, Types.TIMESTAMP, Types.BOOLEAN}; }
    }
}
