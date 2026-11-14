package co.itm.prestamo.excepciones;

public class TransicionInvalidaException extends NegocioException {
    public TransicionInvalidaException(String mensaje) { super(mensaje, 409); }
}
