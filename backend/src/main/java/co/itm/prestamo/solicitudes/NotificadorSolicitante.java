package co.itm.prestamo.solicitudes;

import co.itm.prestamo.dominio.Solicitud;
import co.itm.prestamo.puertos.NotificacionPort;

/** HU-04: avisa al solicitante del resultado. */
public class NotificadorSolicitante implements SolicitudObserver {
    private final NotificacionPort notificaciones;
    public NotificadorSolicitante(NotificacionPort notificaciones) { this.notificaciones = notificaciones; }
    @Override public void solicitudDecidida(Solicitud s) {
        notificaciones.notificar(s.getSolicitante().getId(),
            "Su solicitud #" + s.getId() + " de «" + s.getRecurso().getNombre() + "» para el " + s.getFecha()
            + " fue " + s.getEstado().getEtiqueta().toUpperCase());
    }
}
