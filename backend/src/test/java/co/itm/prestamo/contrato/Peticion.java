package co.itm.prestamo.contrato;

import co.itm.prestamo.dominio.Usuario;
import java.util.List;
import java.util.Map;

/** Petición ya interpretada: método, partes de la ruta tras el recurso, parámetros, cuerpo JSON y usuario autenticado. */
public record Peticion(String metodo, List<String> partes, Map<String, String> consulta, Map<String, String> cuerpo, Usuario actor, String token) {
    public boolean es(String metodo, int partes) { return this.metodo.equals(metodo) && this.partes.size() == partes; }
    public boolean es(String metodo, String parte0) { return this.metodo.equals(metodo) && partes.size() == 1 && partes.get(0).equals(parte0); }
    public long idEn(int posicion) {
        try { return Long.parseLong(partes.get(posicion)); } catch (NumberFormatException e) { throw new co.itm.prestamo.excepciones.CamposIncompletosException("Identificador no válido"); }
    }
}
