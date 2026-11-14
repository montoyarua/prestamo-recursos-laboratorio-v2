package co.itm.prestamo.excepciones;

/** HU-10, escenario 2 (RN-07): ya existe una cuenta con ese correo. */
public class CorreoDuplicadoException extends NegocioException {
    public CorreoDuplicadoException(String mensaje) { super(mensaje, 409); }
}
