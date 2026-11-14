package co.itm.prestamo.excepciones;

/** HU-09, escenario 2: usuario o contraseña incorrectos. */
public class CredencialesInvalidasException extends NegocioException {
    public CredencialesInvalidasException(String mensaje) { super(mensaje, 401); }
}
