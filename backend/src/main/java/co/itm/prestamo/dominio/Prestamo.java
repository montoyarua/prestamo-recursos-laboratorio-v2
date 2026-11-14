package co.itm.prestamo.dominio;

import co.itm.prestamo.excepciones.PrestamoYaFinalizadoException;
import java.time.LocalDateTime;

/** Préstamo que nace al aprobarse una solicitud (Activo) y termina al registrarse la devolución (Finalizado). */
public class Prestamo {
    private long id; private final Solicitud solicitud; private final LocalDateTime fechaCreacion;
    private LocalDateTime fechaDevolucion; private EstadoPrestamo estado = EstadoPrestamo.ACTIVO;
    public Prestamo(Solicitud solicitud, LocalDateTime fechaCreacion) { this.solicitud = solicitud; this.fechaCreacion = fechaCreacion; }
    /** Reconstruye un préstamo guardado desde la base de datos. */
    public static Prestamo restaurar(long id, Solicitud solicitud, LocalDateTime fechaCreacion, EstadoPrestamo estado, LocalDateTime fechaDevolucion) {
        Prestamo p = new Prestamo(solicitud, fechaCreacion); p.id = id; p.estado = estado; p.fechaDevolucion = fechaDevolucion; return p;
    }
    /** HU-08: solo un préstamo «Activo» puede finalizarse. */
    public void finalizar(LocalDateTime cuando) {
        if (estado == EstadoPrestamo.FINALIZADO) throw new PrestamoYaFinalizadoException("El recurso ya fue devuelto");
        estado = EstadoPrestamo.FINALIZADO; fechaDevolucion = cuando;
    }
    public boolean estaActivo() { return estado == EstadoPrestamo.ACTIVO; }
    public long getId() { return id; } public void setId(long id) { this.id = id; }
    public Solicitud getSolicitud() { return solicitud; } public EstadoPrestamo getEstado() { return estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; } public LocalDateTime getFechaDevolucion() { return fechaDevolucion; }
}
