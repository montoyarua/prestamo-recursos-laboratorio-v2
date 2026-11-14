package co.itm.prestamo.solicitudes;

import co.itm.prestamo.dominio.Solicitud;
import co.itm.prestamo.excepciones.ConflictoHorarioException;
import co.itm.prestamo.puertos.DisponibilidadPort;

/** RN-05: un usuario no puede tener dos reservas aprobadas que se crucen. */
public class ValidadorConflictoUsuario extends ValidadorSolicitud {
    private final DisponibilidadPort disponibilidad;
    public ValidadorConflictoUsuario(DisponibilidadPort disponibilidad) { this.disponibilidad = disponibilidad; }
    @Override protected void comprobar(Solicitud s) {
        if (disponibilidad.usuarioOcupado(s.getSolicitante().getId(), s.getFecha(), s.getHoraInicio(), s.getHoraFin(), s.getId()))
            throw new ConflictoHorarioException("El usuario ya tiene una reserva aprobada que se cruza con ese horario");
    }
}
