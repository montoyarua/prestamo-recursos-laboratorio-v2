package co.itm.prestamo.dominio;

public class EstadoPendiente implements EstadoSolicitudState {
    @Override public EstadoSolicitud tipo() { return EstadoSolicitud.PENDIENTE; }
    @Override public void aprobar(Solicitud s) { s.cambiarEstado(new EstadoAprobada()); }
    @Override public void rechazar(Solicitud s) { s.cambiarEstado(new EstadoRechazada()); }
}
