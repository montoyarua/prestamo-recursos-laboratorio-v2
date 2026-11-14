package co.itm.prestamo.solicitudes;

import co.itm.prestamo.dominio.Solicitud;
import co.itm.prestamo.excepciones.ConflictoHorarioException;
import co.itm.prestamo.puertos.DisponibilidadPort;

/** RN-01 y RN-04: un recurso no se reserva si tiene una solicitud aprobada que se cruza o un préstamo sin devolver. */
public class ValidadorConflictoRecurso extends ValidadorSolicitud {
    private final DisponibilidadPort disponibilidad;
    public ValidadorConflictoRecurso(DisponibilidadPort disponibilidad) { this.disponibilidad = disponibilidad; }
    @Override protected void comprobar(Solicitud s) {
        if (disponibilidad.recursoOcupado(s.getRecurso().getId(), s.getFecha(), s.getHoraInicio(), s.getHoraFin(), s.getId()))
            throw new ConflictoHorarioException("El recurso no está disponible en ese horario: ya está reservado o prestado");
    }
}
