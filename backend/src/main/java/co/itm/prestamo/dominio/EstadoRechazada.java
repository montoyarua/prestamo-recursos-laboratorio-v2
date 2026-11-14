package co.itm.prestamo.dominio;

import co.itm.prestamo.excepciones.TransicionInvalidaException;

public class EstadoRechazada implements EstadoSolicitudState {
    @Override public EstadoSolicitud tipo() { return EstadoSolicitud.RECHAZADA; }
    @Override public void aprobar(Solicitud s) { throw new TransicionInvalidaException("La solicitud ya fue rechazada y no puede aprobarse"); }
    @Override public void rechazar(Solicitud s) { throw new TransicionInvalidaException("La solicitud ya fue rechazada"); }
}
