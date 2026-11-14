package co.itm.prestamo.spring;

import co.itm.prestamo.dominio.Prestamo;
import co.itm.prestamo.dominio.Usuario;
import co.itm.prestamo.prestamos.PrestamoOperaciones;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/** Ejecuta la devolución en una transacción: el préstamo y el estado del recurso se guardan juntos o no se guarda ninguno. */
public class PrestamosTransaccionales implements PrestamoOperaciones {
    private final PrestamoOperaciones real; private final TransactionTemplate transaccion; private final JdbcTemplate jdbc;
    public PrestamosTransaccionales(PrestamoOperaciones real, TransactionTemplate transaccion, JdbcTemplate jdbc) {
        this.real = real; this.transaccion = transaccion; this.jdbc = jdbc;
    }
    @Override public Prestamo registrarDevolucion(Usuario actor, long id) {
        return transaccion.execute(estado -> {
            jdbc.execute("SELECT pg_advisory_xact_lock(" + SolicitudesTransaccionales.BLOQUEO_SOLICITUDES + ")");
            return real.registrarDevolucion(actor, id);
        });
    }
    @Override public List<Prestamo> listarPrestamosActivos(Usuario actor) { return real.listarPrestamosActivos(actor); }
    @Override public List<Prestamo> listarPrestamosUsuario(Usuario actor) { return real.listarPrestamosUsuario(actor); }
}
