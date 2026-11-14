package co.itm.prestamo.solicitudes;

import java.time.LocalDate;
import java.time.LocalTime;

/** Datos ya tipados de una solicitud (HU-02). El controlador convierte el formulario y el servicio no conoce el texto. */
public record NuevaSolicitud(Long recursoId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, String motivo) { }
