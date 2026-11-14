package co.itm.prestamo.excepciones;

public class CamposIncompletosException extends NegocioException {
    public CamposIncompletosException(String mensaje) { super(mensaje, 400); }
}
