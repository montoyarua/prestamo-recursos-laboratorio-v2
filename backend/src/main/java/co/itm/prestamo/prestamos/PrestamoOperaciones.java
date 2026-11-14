package co.itm.prestamo.prestamos;

import co.itm.prestamo.dominio.Prestamo;
import co.itm.prestamo.dominio.Usuario;
import java.util.List;

/** Interfaz común del Proxy de Préstamos: la comparten PrestamoService (real) y PrestamoServiceProxy. */
public interface PrestamoOperaciones {
    Prestamo registrarDevolucion(Usuario actor, long prestamoId);
    List<Prestamo> listarPrestamosActivos(Usuario actor);
    List<Prestamo> listarPrestamosUsuario(Usuario actor);
}
