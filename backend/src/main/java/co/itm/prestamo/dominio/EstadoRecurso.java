package co.itm.prestamo.dominio;

public enum EstadoRecurso {
    DISPONIBLE("Disponible"), PRESTADO("Prestado");
    private final String etiqueta;
    EstadoRecurso(String etiqueta) { this.etiqueta = etiqueta; }
    public String getEtiqueta() { return etiqueta; }
}
