package co.itm.prestamo.excepciones;

/** Error de regla de negocio. Lleva el código HTTP con el que la API lo informa. */
public class NegocioException extends RuntimeException {
    private final int estadoHttp;
    public NegocioException(String mensaje, int estadoHttp) { super(mensaje); this.estadoHttp = estadoHttp; }
    public int getEstadoHttp() { return estadoHttp; }
}
