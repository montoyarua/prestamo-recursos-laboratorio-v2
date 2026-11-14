package co.itm.prestamo.excepciones;

public class AccesoDenegadoException extends NegocioException {
    public AccesoDenegadoException(String mensaje) { super(mensaje, 403); }
}
