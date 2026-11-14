package co.itm.prestamo.solicitudes;

import co.itm.prestamo.dominio.Solicitud;
import co.itm.prestamo.dominio.Usuario;
import java.util.List;

/** Interfaz común del Proxy: la comparten SolicitudService (real) y SolicitudServiceProxy. */
public interface SolicitudOperaciones {
    Solicitud registrarSolicitud(Usuario actor, NuevaSolicitud datos);
    Solicitud aprobarSolicitud(Usuario actor, long solicitudId);
    Solicitud rechazarSolicitud(Usuario actor, long solicitudId);
    List<Solicitud> listarPendientes(Usuario actor);
    List<Solicitud> listarDeUsuario(Usuario actor);
}
