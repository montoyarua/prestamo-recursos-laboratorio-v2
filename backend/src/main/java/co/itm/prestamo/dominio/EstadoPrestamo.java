package co.itm.prestamo.dominio;

public enum EstadoPrestamo {
    ACTIVO("Activo"), FINALIZADO("Finalizado");
    private final String etiqueta;
    EstadoPrestamo(String etiqueta) { this.etiqueta = etiqueta; }
    public String getEtiqueta() { return etiqueta; }
}
