package co.itm.prestamo.excepciones;

public class HorarioInvalidoException extends NegocioException {
    public HorarioInvalidoException(String mensaje) { super(mensaje, 400); }
}
