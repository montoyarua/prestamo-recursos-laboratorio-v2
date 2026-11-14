package co.itm.prestamo.contrato;

import co.itm.prestamo.persistencia.Fila;
import co.itm.prestamo.persistencia.Filas.*;
import co.itm.prestamo.persistencia.Tabla;
import co.itm.prestamo.persistencia.Tablas;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;

/**
 * Solo para pruebas: tablas que hablan con un PostgreSQL REAL usando el cliente de línea de comandos «psql» (no necesita Spring ni el
 * controlador JDBC). Permiten ejecutar las mismas pruebas de reglas de negocio contra el esquema real (docs/base_de_datos.sql),
 * con su restricción de exclusión y su disparador. Variables: PGHOST, PGPORT, PGUSER, PGPASSWORD, PGDATABASE.
 */
public final class TablasPsql {
    private TablasPsql() { }

    public static String psql(String sql) {
        try {
            ProcessBuilder pb = new ProcessBuilder("psql", "-X", "-q", "-t", "-A", "-F", "\u001f", "-P", "null=NULL_", "-v", "ON_ERROR_STOP=1");
            pb.environment().put("PGCLIENTENCODING", "UTF8");   // el SQL viaja por la entrada estándar en UTF-8 (los argumentos de línea de comandos pierden las tildes)
            pb.redirectErrorStream(true);
            Process p = pb.start();
            try (var o = p.getOutputStream()) { o.write((sql + ";\n").getBytes(StandardCharsets.UTF_8)); }
            String salida = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            if (p.waitFor() != 0) throw new IllegalStateException("psql falló: " + salida.trim());
            return salida;
        } catch (IOException | InterruptedException e) { throw new IllegalStateException(e); }
    }

    /** Vacía las tablas y reinicia los identificadores, para que cada escenario de prueba parta de cero. */
    public static Tablas limpias() {
        psql("TRUNCATE notificacion, prestamo, solicitud, recurso, usuario RESTART IDENTITY CASCADE");
        return new Tablas(new Usuarios(), new Recursos(), new Solicitudes(), new Prestamos(), new Notificaciones());
    }

    static String lit(Object v) {
        if (v == null) return "NULL";
        if (v instanceof Boolean || v instanceof Number) return v.toString();
        return "'" + v.toString().replace("'", "''") + "'";
    }
    static String t(String[] c, int i) { return c[i].equals("NULL_") ? null : c[i]; }

    abstract static class Base<F extends Fila<F>> implements Tabla<F> {
        abstract String tabla();
        abstract String[] columnas();
        abstract Object[] valores(F f);
        abstract F desde(String[] c);          // c[0] = id, luego las columnas en orden

        private List<F> consultar(String sql) {
            List<F> r = new ArrayList<>();
            for (String linea : psql(sql).split("\n")) if (!linea.isBlank()) r.add(desde(linea.split("\u001f", -1)));
            return r;
        }
        private String todasLasColumnas() { return "id, " + String.join(", ", columnas()); }

        @Override public F guardar(F f) {
            String[] cols = columnas(); Object[] v = valores(f);
            if (f.id() == 0) {
                List<String> lits = new ArrayList<>(); for (Object o : v) lits.add(lit(o));
                String salida = psql("INSERT INTO " + tabla() + " (" + String.join(", ", cols) + ") VALUES (" + String.join(", ", lits) + ") RETURNING id");
                return f.conId(Long.parseLong(salida.trim()));
            }
            List<String> asig = new ArrayList<>(); for (int i = 0; i < cols.length; i++) asig.add(cols[i] + " = " + lit(v[i]));
            psql("UPDATE " + tabla() + " SET " + String.join(", ", asig) + " WHERE id = " + f.id());
            return f;
        }
        @Override public Optional<F> buscar(long id) {
            List<F> r = consultar("SELECT " + todasLasColumnas() + " FROM " + tabla() + " WHERE id = " + id);
            return r.isEmpty() ? Optional.empty() : Optional.of(r.get(0));
        }
        @Override public List<F> todas() { return consultar("SELECT " + todasLasColumnas() + " FROM " + tabla() + " ORDER BY id"); }
    }

    static final class Usuarios extends Base<UsuarioFila> {
        String tabla() { return "usuario"; }
        String[] columnas() { return new String[] {"nombre", "correo", "contrasena", "rol"}; }
        Object[] valores(UsuarioFila f) { return new Object[] {f.nombre(), f.correo(), f.contrasena(), f.rol()}; }
        UsuarioFila desde(String[] c) { return new UsuarioFila(Long.parseLong(c[0]), c[1], c[2], c[3], c[4]); }
    }
    static final class Recursos extends Base<RecursoFila> {
        String tabla() { return "recurso"; }
        String[] columnas() { return new String[] {"tipo", "nombre", "estado", "ubicacion", "capacidad", "marca", "modelo", "numero_serie", "activo"}; }
        Object[] valores(RecursoFila f) { return new Object[] {f.tipo(), f.nombre(), f.estado(), f.ubicacion(), f.capacidad(), f.marca(), f.modelo(), f.numeroSerie(), f.activo()}; }
        RecursoFila desde(String[] c) {
            return new RecursoFila(Long.parseLong(c[0]), c[1], c[2], c[3], t(c, 4), t(c, 5) == null ? null : Integer.valueOf(c[5]), t(c, 6), t(c, 7), t(c, 8), c[9].equals("t"));
        }
    }
    static final class Solicitudes extends Base<SolicitudFila> {
        String tabla() { return "solicitud"; }
        String[] columnas() { return new String[] {"usuario_id", "recurso_id", "fecha", "hora_inicio", "hora_fin", "motivo", "estado", "fecha_registro"}; }
        Object[] valores(SolicitudFila f) { return new Object[] {f.usuarioId(), f.recursoId(), f.fecha(), f.horaInicio(), f.horaFin(), f.motivo(), f.estado(), f.fechaRegistro()}; }
        SolicitudFila desde(String[] c) {
            return new SolicitudFila(Long.parseLong(c[0]), Long.parseLong(c[1]), Long.parseLong(c[2]), LocalDate.parse(c[3]), LocalTime.parse(c[4]), LocalTime.parse(c[5]),
                t(c, 6), c[7], LocalDateTime.parse(c[8].replace(' ', 'T')));
        }
    }
    static final class Prestamos extends Base<PrestamoFila> {
        String tabla() { return "prestamo"; }
        String[] columnas() { return new String[] {"solicitud_id", "estado", "fecha_creacion", "fecha_devolucion"}; }
        Object[] valores(PrestamoFila f) { return new Object[] {f.solicitudId(), f.estado(), f.fechaCreacion(), f.fechaDevolucion()}; }
        PrestamoFila desde(String[] c) {
            return new PrestamoFila(Long.parseLong(c[0]), Long.parseLong(c[1]), c[2], LocalDateTime.parse(c[3].replace(' ', 'T')), t(c, 4) == null ? null : LocalDateTime.parse(c[4].replace(' ', 'T')));
        }
    }
    static final class Notificaciones extends Base<NotificacionFila> {
        String tabla() { return "notificacion"; }
        String[] columnas() { return new String[] {"usuario_id", "mensaje", "fecha_creacion", "leida"}; }
        Object[] valores(NotificacionFila f) { return new Object[] {f.usuarioId(), f.mensaje(), f.fechaCreacion(), f.leida()}; }
        NotificacionFila desde(String[] c) { return new NotificacionFila(Long.parseLong(c[0]), Long.parseLong(c[1]), c[2], LocalDateTime.parse(c[3].replace(' ', 'T')), c[4].equals("t")); }
    }
}
