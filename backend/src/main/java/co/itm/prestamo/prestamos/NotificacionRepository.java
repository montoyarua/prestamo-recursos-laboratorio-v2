package co.itm.prestamo.prestamos;

import co.itm.prestamo.dominio.Notificacion;
import co.itm.prestamo.persistencia.Filas.NotificacionFila;
import co.itm.prestamo.persistencia.Tabla;
import java.util.*;

public class NotificacionRepository {
    private final Tabla<NotificacionFila> tabla;
    public NotificacionRepository(Tabla<NotificacionFila> tabla) { this.tabla = tabla; }
    public Notificacion guardar(Notificacion n) {
        NotificacionFila f = tabla.guardar(new NotificacionFila(n.getId(), n.getUsuarioId(), n.getMensaje(), n.getFechaCreacion(), n.isLeida()));
        n.setId(f.id()); return n;
    }
    public List<Notificacion> deUsuario(long usuarioId) {
        List<Notificacion> r = new ArrayList<>();
        for (NotificacionFila f : tabla.todas())
            if (f.usuarioId() == usuarioId) r.add(Notificacion.restaurar(f.id(), f.usuarioId(), f.mensaje(), f.fechaCreacion(), f.leida()));
        return r;
    }
}
