package co.itm.prestamo.prestamos;

import co.itm.prestamo.dominio.*;
import co.itm.prestamo.persistencia.Filas.PrestamoFila;
import co.itm.prestamo.persistencia.Tabla;
import co.itm.prestamo.solicitudes.SolicitudRepository;
import java.util.*;

public class PrestamoRepository {
    private final Tabla<PrestamoFila> tabla; private final SolicitudRepository solicitudes;
    public PrestamoRepository(Tabla<PrestamoFila> tabla, SolicitudRepository solicitudes) { this.tabla = tabla; this.solicitudes = solicitudes; }
    public Prestamo guardar(Prestamo p) {
        PrestamoFila f = tabla.guardar(new PrestamoFila(p.getId(), p.getSolicitud().getId(), p.getEstado().name(), p.getFechaCreacion(), p.getFechaDevolucion()));
        p.setId(f.id()); return p;
    }
    public Optional<Prestamo> buscar(long id) { return tabla.buscar(id).map(this::aPrestamo); }
    public List<Prestamo> todos() {
        Map<Long, Solicitud> porId = new HashMap<>(); for (Solicitud s : solicitudes.todas()) porId.put(s.getId(), s);
        List<Prestamo> r = new ArrayList<>(); for (PrestamoFila f : tabla.todas()) r.add(aPrestamo(f, porId.get(f.solicitudId())));
        return r;
    }

    private Prestamo aPrestamo(PrestamoFila f) { return aPrestamo(f, solicitudes.buscar(f.solicitudId()).orElse(null)); }
    private Prestamo aPrestamo(PrestamoFila f, Solicitud s) {
        if (s == null) throw new IllegalStateException("Préstamo sin solicitud: " + f.id());
        return Prestamo.restaurar(f.id(), s, f.fechaCreacion(), EstadoPrestamo.valueOf(f.estado()), f.fechaDevolucion());
    }
}
