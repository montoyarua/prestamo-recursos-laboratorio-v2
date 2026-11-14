package co.itm.prestamo.api;

import co.itm.prestamo.dominio.*;
import java.util.*;

/** Convierte el dominio en mapas simples para el JSON de la API (nunca expone la contraseña). */
public final class Vistas {
    private Vistas() { }
    public static Map<String, Object> usuario(Usuario u) {
        Map<String, Object> m = new LinkedHashMap<>(); m.put("id", u.getId()); m.put("nombre", u.getNombre()); m.put("correo", u.getCorreo()); m.put("rol", u.getRol().name()); return m;
    }
    public static Map<String, Object> recurso(Recurso r) {
        Map<String, Object> m = new LinkedHashMap<>(); m.put("id", r.getId()); m.put("tipo", r.getTipo().name()); m.put("nombre", r.getNombre());
        m.put("detalle", r.getDetalle()); m.put("estado", r.getEstado().getEtiqueta());
        if (r instanceof Laboratorio l) { m.put("ubicacion", l.getUbicacion()); m.put("capacidad", l.getCapacidad()); }
        if (r instanceof Equipo e) { m.put("marca", e.getMarca()); m.put("modelo", e.getModelo()); m.put("numeroSerie", e.getNumeroSerie()); }
        return m;
    }
    public static Map<String, Object> solicitud(Solicitud s) {
        Map<String, Object> m = new LinkedHashMap<>(); m.put("id", s.getId()); m.put("recurso", s.getRecurso().getNombre());
        m.put("solicitante", s.getSolicitante().getNombre()); m.put("fecha", s.getFecha().toString());
        m.put("horaInicio", s.getHoraInicio().toString()); m.put("horaFin", s.getHoraFin().toString());
        m.put("motivo", s.getMotivo()); m.put("estado", s.getEstado().getEtiqueta()); return m;
    }
    public static Map<String, Object> prestamo(Prestamo p) {
        Solicitud s = p.getSolicitud(); Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", p.getId()); m.put("recurso", s.getRecurso().getNombre()); m.put("usuario", s.getSolicitante().getNombre());
        m.put("fecha", s.getFecha().toString()); m.put("horaInicio", s.getHoraInicio().toString()); m.put("horaFin", s.getHoraFin().toString());
        m.put("estado", p.getEstado().getEtiqueta());
        m.put("fechaDevolucion", p.getFechaDevolucion() == null ? null : p.getFechaDevolucion().toString()); return m;
    }
    public static Map<String, Object> notificacion(Notificacion n) {
        Map<String, Object> m = new LinkedHashMap<>(); m.put("id", n.getId()); m.put("mensaje", n.getMensaje()); m.put("leida", n.isLeida()); m.put("fecha", n.getFechaCreacion().toString()); return m;
    }
    public static <T> List<Object> lista(Collection<T> items, java.util.function.Function<T, Map<String, Object>> f) {
        List<Object> r = new ArrayList<>(); for (T t : items) r.add(f.apply(t)); return r;
    }
}
