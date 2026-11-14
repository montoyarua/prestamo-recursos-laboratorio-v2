package co.itm.prestamo.dominio;

/**
 * Patrón State: el estado decide qué transiciones permite la solicitud.
 * Ambas operaciones lanzan {@link co.itm.prestamo.excepciones.TransicionInvalidaException} cuando la
 * transición no está permitida desde ese estado (solo «Pendiente» puede aprobarse o rechazarse).
 */
public interface EstadoSolicitudState {
    EstadoSolicitud tipo();
    void aprobar(Solicitud solicitud);
    void rechazar(Solicitud solicitud);
}
