package co.itm.prestamo.dominio;

import java.util.Map;

/** Producto del Factory Method. Un recurso es un laboratorio o un equipo. */
public abstract class Recurso {
    private long id; private String nombre; private EstadoRecurso estado = EstadoRecurso.DISPONIBLE;
    protected Recurso(String nombre) { this.nombre = nombre; }
    public long getId() { return id; } public void setId(long id) { this.id = id; }
    public String getNombre() { return nombre; } public void setNombre(String nombre) { this.nombre = nombre; }
    public EstadoRecurso getEstado() { return estado; }
    public void marcarPrestado() { estado = EstadoRecurso.PRESTADO; }
    public void marcarDisponible() { estado = EstadoRecurso.DISPONIBLE; }
    /** Solo para reconstruir el objeto desde la base de datos. */
    public void restaurarEstado(EstadoRecurso e) { estado = e; }
    public abstract TipoRecurso getTipo();
    public abstract String getDetalle();
    /** Actualiza los datos propios del tipo (ubicación, capacidad, marca...). Ignora los campos vacíos. */
    public abstract void actualizar(Map<String, String> datos);
    protected static boolean hay(Map<String, String> d, String clave) { return d.get(clave) != null && !d.get(clave).isBlank(); }
}
