package co.itm.prestamo.excepciones;

/** HU-08, escenario 2: el préstamo ya había sido devuelto. */
public class PrestamoYaFinalizadoException extends NegocioException {
    public PrestamoYaFinalizadoException(String mensaje) { super(mensaje, 409); }
}
