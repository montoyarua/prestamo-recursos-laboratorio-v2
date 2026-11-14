package co.itm.prestamo.prestamos;

import co.itm.prestamo.dominio.Prestamo;
import co.itm.prestamo.dominio.Usuario;
import co.itm.prestamo.excepciones.AccesoDenegadoException;
import co.itm.prestamo.puertos.AutorizacionPort;
import co.itm.prestamo.puertos.Operacion;
import java.util.List;

/** Proxy de protección sobre Préstamos (RN-02, RNF-02, HU-08 esc. 3): solo el encargado registra devoluciones. */
public class PrestamoServiceProxy implements PrestamoOperaciones {
    private final PrestamoOperaciones real; private final AutorizacionPort autorizacion;
    public PrestamoServiceProxy(PrestamoOperaciones real, AutorizacionPort autorizacion) { this.real = real; this.autorizacion = autorizacion; }
    private void exigirEncargado(Usuario actor) {
        if (actor == null || !autorizacion.tienePermiso(actor, Operacion.REGISTRAR_DEVOLUCION))
            throw new AccesoDenegadoException("Su rol no tiene permiso para esta acción");
    }
    @Override public Prestamo registrarDevolucion(Usuario actor, long id) { exigirEncargado(actor); return real.registrarDevolucion(actor, id); }
    @Override public List<Prestamo> listarPrestamosActivos(Usuario actor) { exigirEncargado(actor); return real.listarPrestamosActivos(actor); }
    @Override public List<Prestamo> listarPrestamosUsuario(Usuario actor) { return real.listarPrestamosUsuario(actor); }   // cada quien ve los suyos
}
