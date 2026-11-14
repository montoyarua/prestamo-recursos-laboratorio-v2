package co.itm.prestamo.dominio;

import java.time.LocalDateTime;

public class Notificacion {
    private long id; private final long usuarioId; private final String mensaje;
    private final LocalDateTime fechaCreacion; private boolean leida;
    public Notificacion(long usuarioId, String mensaje, LocalDateTime fechaCreacion) { this.usuarioId = usuarioId; this.mensaje = mensaje; this.fechaCreacion = fechaCreacion; }
    public static Notificacion restaurar(long id, long usuarioId, String mensaje, LocalDateTime fecha, boolean leida) {
        Notificacion n = new Notificacion(usuarioId, mensaje, fecha); n.id = id; n.leida = leida; return n;
    }
    public long getId() { return id; } public void setId(long id) { this.id = id; }
    public long getUsuarioId() { return usuarioId; } public String getMensaje() { return mensaje; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public boolean isLeida() { return leida; } public void marcarLeida() { leida = true; }
}
