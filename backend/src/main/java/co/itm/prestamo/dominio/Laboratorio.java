package co.itm.prestamo.dominio;

import co.itm.prestamo.excepciones.CamposIncompletosException;
import java.util.Map;

public class Laboratorio extends Recurso {
    private String ubicacion; private int capacidad;
    public Laboratorio(String nombre, String ubicacion, int capacidad) { super(nombre); this.ubicacion = ubicacion; this.capacidad = capacidad; }
    public String getUbicacion() { return ubicacion; } public int getCapacidad() { return capacidad; }
    @Override public TipoRecurso getTipo() { return TipoRecurso.LABORATORIO; }
    @Override public String getDetalle() { return ubicacion + " · capacidad " + capacidad; }
    @Override public void actualizar(Map<String, String> d) {
        if (hay(d, "nombre")) setNombre(d.get("nombre").trim());
        if (hay(d, "ubicacion")) ubicacion = d.get("ubicacion").trim();
        if (hay(d, "capacidad")) {
            try { capacidad = Integer.parseInt(d.get("capacidad").trim()); }
            catch (NumberFormatException e) { throw new CamposIncompletosException("La capacidad debe ser un número"); }
        }
    }
}
