package co.itm.prestamo.solicitudes;

import co.itm.prestamo.dominio.Solicitud;
import co.itm.prestamo.dominio.Usuario;
import co.itm.prestamo.excepciones.AccesoDenegadoException;
import co.itm.prestamo.puertos.AutorizacionPort;
import co.itm.prestamo.puertos.Operacion;
import java.util.List;

/** Patrón Proxy de protección (RN-02, RNF-02): verifica el rol y solo entonces delega en el servicio real. */
public class SolicitudServiceProxy implements SolicitudOperaciones {
    private final SolicitudOperaciones real;
    private final AutorizacionPort autorizacion;
    public SolicitudServiceProxy(SolicitudOperaciones real, AutorizacionPort autorizacion) { this.real = real; this.autorizacion = autorizacion; }

    private void exigir(Usuario actor, Operacion op) {
        if (actor == null || !autorizacion.tienePermiso(actor, op))
            throw new AccesoDenegadoException("Su rol no tiene permiso para esta acción");
    }
    @Override public Solicitud registrarSolicitud(Usuario actor, NuevaSolicitud d) { exigir(actor, Operacion.REGISTRAR_SOLICITUD); return real.registrarSolicitud(actor, d); }
    @Override public Solicitud aprobarSolicitud(Usuario actor, long id) { exigir(actor, Operacion.DECIDIR_SOLICITUD); return real.aprobarSolicitud(actor, id); }
    @Override public Solicitud rechazarSolicitud(Usuario actor, long id) { exigir(actor, Operacion.DECIDIR_SOLICITUD); return real.rechazarSolicitud(actor, id); }
    @Override public List<Solicitud> listarPendientes(Usuario actor) { exigir(actor, Operacion.DECIDIR_SOLICITUD); return real.listarPendientes(actor); }
    @Override public List<Solicitud> listarDeUsuario(Usuario actor) { exigir(actor, Operacion.REGISTRAR_SOLICITUD); return real.listarDeUsuario(actor); }
}
