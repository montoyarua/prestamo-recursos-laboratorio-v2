package co.itm.prestamo.dominio;

import java.util.Map;

public class Equipo extends Recurso {
    private String marca; private String modelo; private String numeroSerie;
    public Equipo(String nombre, String marca, String modelo, String numeroSerie) {
        super(nombre); this.marca = marca; this.modelo = modelo; this.numeroSerie = numeroSerie;
    }
    public String getMarca() { return marca; } public String getModelo() { return modelo; } public String getNumeroSerie() { return numeroSerie; }
    @Override public TipoRecurso getTipo() { return TipoRecurso.EQUIPO; }
    @Override public String getDetalle() { return marca + " " + modelo + " · serie " + numeroSerie; }
    @Override public void actualizar(Map<String, String> d) {
        if (hay(d, "nombre")) setNombre(d.get("nombre").trim());
        if (hay(d, "marca")) marca = d.get("marca").trim();
        if (hay(d, "modelo")) modelo = d.get("modelo").trim();
        if (hay(d, "numeroSerie")) numeroSerie = d.get("numeroSerie").trim();
    }
}
