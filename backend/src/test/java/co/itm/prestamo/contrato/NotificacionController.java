package co.itm.prestamo.contrato;

import co.itm.prestamo.api.Vistas;
import co.itm.prestamo.api.Convertir;

import co.itm.prestamo.excepciones.RecursoNoEncontradoException;
import co.itm.prestamo.prestamos.NotificacionService;
import java.util.Map;

/** HU-04: GET /api/notificaciones y POST /api/notificaciones/leidas. */
public class NotificacionController implements Controlador {
    private final NotificacionService notificaciones;
    public NotificacionController(NotificacionService notificaciones) { this.notificaciones = notificaciones; }
    @Override public Object manejar(Peticion p) {
        if (p.es("GET", 0)) return Vistas.lista(notificaciones.deUsuario(p.actor().getId()), Vistas::notificacion);
        if (p.es("POST", "leidas")) { notificaciones.marcarLeidas(p.actor().getId()); return Map.of("ok", true); }
        throw new RecursoNoEncontradoException("Ruta no encontrada");
    }
}
