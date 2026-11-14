package co.itm.prestamo.spring;

import java.util.LinkedHashMap;
import java.util.Map;

/** Los formularios llegan como JSON; los servicios esperan textos, así que se convierte cada valor a String (los nulos se omiten). */
final class Cuerpo {
    private Cuerpo() { }
    static Map<String, String> textos(Map<String, Object> json) {
        Map<String, String> r = new LinkedHashMap<>();
        if (json != null) json.forEach((k, v) -> { if (v != null) r.put(k, String.valueOf(v)); });
        return r;
    }
}
