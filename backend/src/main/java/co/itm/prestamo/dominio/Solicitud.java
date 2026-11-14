package co.itm.prestamo.dominio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/** Contexto del patrón State: delega aprobar() y rechazar() en su estado actual. */
public class Solicitud {
    private long id;
    private final LocalDateTime fechaRegistro;
    private final Usuario solicitante;
    private final Recurso recurso;
    private final LocalDate fecha; private final LocalTime horaInicio; private final LocalTime horaFin;
    private final String motivo;
    private EstadoSolicitudState estado = new EstadoPendiente(); // RN-03: nace «Pendiente»

    public Solicitud(Usuario solicitante, Recurso recurso, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, String motivo, LocalDateTime fechaRegistro) {
        this.solicitante = solicitante; this.recurso = recurso; this.fecha = fecha;
        this.horaInicio = horaInicio; this.horaFin = horaFin; this.motivo = motivo; this.fechaRegistro = fechaRegistro;
    }
    /** Reconstruye una solicitud guardada (con su estado) desde la base de datos. */
    public static Solicitud restaurar(long id, Usuario solicitante, Recurso recurso, LocalDate fecha, LocalTime ini, LocalTime fin,
                                      String motivo, LocalDateTime fechaRegistro, EstadoSolicitud estado) {
        Solicitud s = new Solicitud(solicitante, recurso, fecha, ini, fin, motivo, fechaRegistro);
        s.id = id;
        s.estado = switch (estado) { case PENDIENTE -> new EstadoPendiente(); case APROBADA -> new EstadoAprobada(); case RECHAZADA -> new EstadoRechazada(); };
        return s;
    }
    public void aprobar() { estado.aprobar(this); }
    public void rechazar() { estado.rechazar(this); }
    void cambiarEstado(EstadoSolicitudState nuevo) { this.estado = nuevo; }

    public long getId() { return id; } public void setId(long id) { this.id = id; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public Usuario getSolicitante() { return solicitante; } public Recurso getRecurso() { return recurso; }
    public LocalDate getFecha() { return fecha; } public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin() { return horaFin; } public String getMotivo() { return motivo; }
    public EstadoSolicitud getEstado() { return estado.tipo(); }
    public boolean estaAprobada() { return getEstado() == EstadoSolicitud.APROBADA; }
    public boolean estaPendiente() { return getEstado() == EstadoSolicitud.PENDIENTE; }
}
