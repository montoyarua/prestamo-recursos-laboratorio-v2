package co.itm.prestamo.prestamos;

import co.itm.prestamo.dominio.Notificacion;
import co.itm.prestamo.puertos.Atomicidad;
import co.itm.prestamo.puertos.NotificacionPort;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;

/** HU-04: guarda los avisos dentro del sistema (sin correo ni celular). */
public class NotificacionService implements NotificacionPort {
    private final NotificacionRepository notificaciones; private final Clock reloj; private final Atomicidad atomicidad;
    public NotificacionService(NotificacionRepository notificaciones, Clock reloj, Atomicidad atomicidad) {
        this.notificaciones = notificaciones; this.reloj = reloj; this.atomicidad = atomicidad;
    }
    @Override public synchronized void notificar(long usuarioId, String mensaje) {
        notificaciones.guardar(new Notificacion(usuarioId, mensaje, LocalDateTime.now(reloj)));
    }
    public synchronized List<Notificacion> deUsuario(long usuarioId) {
        return notificaciones.deUsuario(usuarioId);
    }
    public void marcarLeidas(long usuarioId) { atomicidad.ejecutar(() -> { marcar(usuarioId); return null; }); }
    private synchronized void marcar(long usuarioId) { for (Notificacion n : notificaciones.deUsuario(usuarioId)) { n.marcarLeida(); notificaciones.guardar(n); } }
}
