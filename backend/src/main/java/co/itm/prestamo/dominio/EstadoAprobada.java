package co.itm.prestamo.dominio;

import co.itm.prestamo.excepciones.TransicionInvalidaException;

public class EstadoAprobada implements EstadoSolicitudState {
    @Override public EstadoSolicitud tipo() { return EstadoSolicitud.APROBADA; }
    @Override public void aprobar(Solicitud s) { throw new TransicionInvalidaException("La solicitud ya fue aprobada"); }
    @Override public void rechazar(Solicitud s) { throw new TransicionInvalidaException("La solicitud ya fue aprobada y no puede rechazarse"); }
}
