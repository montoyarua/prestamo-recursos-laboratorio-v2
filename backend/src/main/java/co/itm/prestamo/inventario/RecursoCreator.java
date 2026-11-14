package co.itm.prestamo.inventario;

import co.itm.prestamo.dominio.Recurso;
import co.itm.prestamo.excepciones.CamposIncompletosException;
import java.util.*;

/** Patrón Factory Method: cada creador concreto decide qué tipo de Recurso fabrica. */
public abstract class RecursoCreator {
    protected abstract List<String> camposObligatorios();
    protected abstract Recurso crearRecurso(Map<String, String> datos);   // el método de fábrica

    public final Recurso nuevoRecurso(Map<String, String> datos) {
        List<String> faltan = new ArrayList<>();
        for (String c : camposObligatorios()) { String v = datos.get(c); if (v == null || v.isBlank()) faltan.add(c); }
        if (!faltan.isEmpty()) throw new CamposIncompletosException("Faltan campos obligatorios: " + String.join(", ", faltan));
        return crearRecurso(datos);
    }
    protected static String texto(Map<String, String> d, String clave) { return d.get(clave).trim(); }
}
