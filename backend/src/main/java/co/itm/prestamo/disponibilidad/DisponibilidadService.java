package co.itm.prestamo.disponibilidad;

import co.itm.prestamo.dominio.*;
import co.itm.prestamo.prestamos.PrestamoService;
import co.itm.prestamo.puertos.*;
import co.itm.prestamo.solicitudes.SolicitudRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Consulta de Disponibilidad. Reglas: RN-01 y RN-05 comparan contra reservas aprobadas. RN-04: si el horario de un
 * préstamo activo ya terminó y el encargado no registró la devolución, el recurso queda ocupado desde el inicio de ese
 * préstamo en adelante, hasta que se registre la devolución (HU-08). Antes de terminar su horario, el préstamo solo
 * ocupa su propio rango (así dos reservas seguidas del mismo día no se estorban).
 */
public class DisponibilidadService implements DisponibilidadPort, RecursosVigentesPort {
    private final SolicitudRepository solicitudes; private final PrestamoService prestamos; private final Clock reloj;
    public DisponibilidadService(SolicitudRepository solicitudes, PrestamoService prestamos, Clock reloj) { this.solicitudes = solicitudes; this.prestamos = prestamos; this.reloj = reloj; }

    private static boolean seCruzan(Solicitud s, LocalDate f, LocalTime ini, LocalTime fin) {
        return s.getFecha().equals(f) && s.getHoraInicio().isBefore(fin) && ini.isBefore(s.getHoraFin());
    }
    /** Una reserva aprobada bloquea mientras no se haya devuelto el recurso. */
    private boolean bloquea(Solicitud s) { return s.estaAprobada() && !prestamos.estaDevuelta(s.getId()); }

    @Override public boolean recursoOcupado(long recursoId, LocalDate f, LocalTime ini, LocalTime fin, long excluir) {
        for (Solicitud s : solicitudes.todas())
            if (s.getId() != excluir && s.getRecurso().getId() == recursoId && bloquea(s) && seCruzan(s, f, ini, fin)) return true;
        LocalDateTime finConsulta = LocalDateTime.of(f, fin), ahora = LocalDateTime.now(reloj);
        for (Prestamo p : prestamos.activosDeRecurso(recursoId)) {                  // RN-04: préstamo vencido sin devolver
            Solicitud o = p.getSolicitud();
            boolean vencido = ahora.isAfter(LocalDateTime.of(o.getFecha(), o.getHoraFin()));
            if (o.getId() != excluir && vencido && LocalDateTime.of(o.getFecha(), o.getHoraInicio()).isBefore(finConsulta)) return true;
        }
        return false;
    }
    @Override public java.util.Set<Long> recursosOcupados(LocalDate f, LocalTime ini, LocalTime fin) {
        java.util.Set<Long> devueltas = prestamos.solicitudesDevueltas(), ocupados = new java.util.HashSet<>();
        for (Solicitud s : solicitudes.todas())
            if (s.estaAprobada() && !devueltas.contains(s.getId()) && seCruzan(s, f, ini, fin)) ocupados.add(s.getRecurso().getId());
        LocalDateTime finConsulta = LocalDateTime.of(f, fin), ahora = LocalDateTime.now(reloj);
        for (Prestamo p : prestamos.todosActivos()) {                               // RN-04: préstamo vencido sin devolver
            Solicitud o = p.getSolicitud();
            boolean vencido = ahora.isAfter(LocalDateTime.of(o.getFecha(), o.getHoraFin()));
            if (vencido && LocalDateTime.of(o.getFecha(), o.getHoraInicio()).isBefore(finConsulta)) ocupados.add(o.getRecurso().getId());
        }
        return ocupados;
    }
    @Override public boolean usuarioOcupado(long usuarioId, LocalDate f, LocalTime ini, LocalTime fin, long excluir) {
        for (Solicitud s : solicitudes.todas())
            if (s.getId() != excluir && s.getSolicitante().getId() == usuarioId && bloquea(s) && seCruzan(s, f, ini, fin)) return true;
        return false;
    }
    @Override public boolean tieneVigentes(long recursoId) {
        for (Solicitud s : solicitudes.todas())
            if (s.getRecurso().getId() == recursoId && (s.estaPendiente() || bloquea(s))) return true;
        return prestamos.tienePrestamoActivo(recursoId);
    }
}
