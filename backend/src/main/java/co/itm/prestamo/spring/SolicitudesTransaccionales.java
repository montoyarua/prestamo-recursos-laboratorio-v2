package co.itm.prestamo.spring;

import co.itm.prestamo.dominio.Solicitud;
import co.itm.prestamo.dominio.Usuario;
import co.itm.prestamo.solicitudes.NuevaSolicitud;
import co.itm.prestamo.solicitudes.SolicitudOperaciones;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Decorador que ejecuta cada operación de escritura de solicitudes dentro de una transacción de PostgreSQL (RNF-05).
 * Al iniciar toma un bloqueo consultivo de la transacción: dos registros o aprobaciones simultáneas se procesan una tras otra,
 * y si falla cualquier paso (por ejemplo, crear el préstamo) se deshace todo. La restricción de exclusión de la base de datos
 * es la última barrera contra dos reservas aprobadas que se cruzan (RN-01).
 */
public class SolicitudesTransaccionales implements SolicitudOperaciones {
    static final long BLOQUEO_SOLICITUDES = 7301L;
    private final SolicitudOperaciones real; private final TransactionTemplate transaccion; private final JdbcTemplate jdbc;
    public SolicitudesTransaccionales(SolicitudOperaciones real, TransactionTemplate transaccion, JdbcTemplate jdbc) {
        this.real = real; this.transaccion = transaccion; this.jdbc = jdbc;
    }
    private <T> T enTransaccion(Supplier<T> trabajo) {
        return transaccion.execute(estado -> {
            jdbc.execute("SELECT pg_advisory_xact_lock(" + BLOQUEO_SOLICITUDES + ")");
            return trabajo.get();
        });
    }
    @Override public Solicitud registrarSolicitud(Usuario actor, NuevaSolicitud datos) { return enTransaccion(() -> real.registrarSolicitud(actor, datos)); }
    @Override public Solicitud aprobarSolicitud(Usuario actor, long id) { return enTransaccion(() -> real.aprobarSolicitud(actor, id)); }
    @Override public Solicitud rechazarSolicitud(Usuario actor, long id) { return enTransaccion(() -> real.rechazarSolicitud(actor, id)); }
    @Override public List<Solicitud> listarPendientes(Usuario actor) { return real.listarPendientes(actor); }
    @Override public List<Solicitud> listarDeUsuario(Usuario actor) { return real.listarDeUsuario(actor); }
}
