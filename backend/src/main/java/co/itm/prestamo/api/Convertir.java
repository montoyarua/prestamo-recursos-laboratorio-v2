package co.itm.prestamo.api;

import co.itm.prestamo.excepciones.HorarioInvalidoException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/** Convierte el texto del formulario en fechas y horas (vacío = null; formato incorrecto = horario no válido). */
public final class Convertir {
    private Convertir() { }
    public static LocalDate fecha(String t) {
        try { return (t == null || t.isBlank()) ? null : LocalDate.parse(t.trim()); }
        catch (DateTimeParseException e) { throw new HorarioInvalidoException("La fecha no tiene un formato válido"); }
    }
    public static LocalTime hora(String t) {
        try { return (t == null || t.isBlank()) ? null : LocalTime.parse(t.trim()); }
        catch (DateTimeParseException e) { throw new HorarioInvalidoException("La hora no tiene un formato válido"); }
    }
}
