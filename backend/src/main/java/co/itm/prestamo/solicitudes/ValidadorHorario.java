package co.itm.prestamo.solicitudes;

import co.itm.prestamo.dominio.Solicitud;
import co.itm.prestamo.excepciones.HorarioInvalidoException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * HU-02, escenario 3: la fecha no puede ser pasada, si es hoy la hora de inicio no puede haber pasado, y la hora de fin
 * debe ser posterior a la de inicio. También está en la cadena de aprobación: una solicitud pendiente cuyo horario ya
 * pasó no puede aprobarse (si se aprobara, nacería un préstamo vencido que bloquearía el recurso por RN-04).
 */
public class ValidadorHorario extends ValidadorSolicitud {
    private final Clock reloj;
    public ValidadorHorario(Clock reloj) { this.reloj = reloj; }
    @Override protected void comprobar(Solicitud s) {
        if (s.getFecha().isBefore(LocalDate.now(reloj))) throw new HorarioInvalidoException("El horario no es válido: la fecha ya pasó");
        if (LocalDateTime.of(s.getFecha(), s.getHoraInicio()).isBefore(LocalDateTime.now(reloj)))
            throw new HorarioInvalidoException("El horario no es válido: la hora de inicio ya pasó");
        if (!s.getHoraInicio().isBefore(s.getHoraFin())) throw new HorarioInvalidoException("El horario no es válido: la hora de inicio debe ser anterior a la hora de fin");
    }
}
