package co.itm.prestamo.excepciones;

/** La petición no trae una sesión válida (HU-09). */
public class NoAutenticadoException extends NegocioException {
    public NoAutenticadoException(String mensaje) { super(mensaje, 401); }
}
