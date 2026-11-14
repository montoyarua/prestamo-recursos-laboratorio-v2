package co.itm.prestamo.excepciones;

public class ConflictoHorarioException extends NegocioException {
    public ConflictoHorarioException(String mensaje) { super(mensaje, 409); }
}
