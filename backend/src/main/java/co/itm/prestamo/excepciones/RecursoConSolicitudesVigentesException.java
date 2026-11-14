package co.itm.prestamo.excepciones;

public class RecursoConSolicitudesVigentesException extends NegocioException {
    public RecursoConSolicitudesVigentesException(String mensaje) { super(mensaje, 409); }
}
