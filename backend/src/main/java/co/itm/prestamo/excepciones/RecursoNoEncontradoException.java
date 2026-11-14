package co.itm.prestamo.excepciones;

public class RecursoNoEncontradoException extends NegocioException {
    public RecursoNoEncontradoException(String mensaje) { super(mensaje, 404); }
}
