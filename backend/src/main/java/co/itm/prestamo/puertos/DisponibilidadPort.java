package co.itm.prestamo.puertos;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Puerto hacia Consulta de Disponibilidad. Un recurso está ocupado si tiene una solicitud aprobada que se cruza
 * (RN-01) o un préstamo activo cuyo inicio es anterior al fin consultado, hasta que se registre la devolución (RN-04).
 */
public interface DisponibilidadPort {
    boolean recursoOcupado(long recursoId, LocalDate fecha, LocalTime ini, LocalTime fin, long excluirSolicitudId);
    /** Ids de todos los recursos ocupados en la fecha y el rango (mismas reglas que recursoOcupado, en una sola pasada). */
    java.util.Set<Long> recursosOcupados(LocalDate fecha, LocalTime inicio, LocalTime fin);
    /** RN-05: el usuario ya tiene una reserva aprobada, sin devolver, que se cruza con el rango. */
    boolean usuarioOcupado(long usuarioId, LocalDate fecha, LocalTime ini, LocalTime fin, long excluirSolicitudId);
}
