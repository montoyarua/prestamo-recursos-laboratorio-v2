package co.itm.prestamo.inventario;

import co.itm.prestamo.dominio.*;
import co.itm.prestamo.persistencia.Filas.RecursoFila;
import co.itm.prestamo.persistencia.Tabla;
import java.util.*;

/** Repositorio de recursos (laboratorios y equipos). Eliminar es una baja lógica: el recurso deja de listarse pero su historial se conserva. */
public class RecursoRepository {
    private final Tabla<RecursoFila> tabla;
    public RecursoRepository(Tabla<RecursoFila> tabla) { this.tabla = tabla; }

    public Recurso guardar(Recurso r) {
        boolean activo = r.getId() == 0 || tabla.buscar(r.getId()).map(RecursoFila::activo).orElse(true);   // guardar no «revive» un recurso eliminado
        RecursoFila base = new RecursoFila(r.getId(), r.getTipo().name(), r.getNombre(), r.getEstado().name(), null, null, null, null, null, activo);
        if (r instanceof Laboratorio l) base = new RecursoFila(r.getId(), base.tipo(), base.nombre(), base.estado(), l.getUbicacion(), l.getCapacidad(), null, null, null, activo);
        if (r instanceof Equipo e) base = new RecursoFila(r.getId(), base.tipo(), base.nombre(), base.estado(), null, null, e.getMarca(), e.getModelo(), e.getNumeroSerie(), activo);
        r.setId(tabla.guardar(base).id());
        return r;
    }
    /** Solo recursos que no fueron eliminados. */
    public Optional<Recurso> buscar(long id) { return tabla.buscar(id).filter(RecursoFila::activo).map(RecursoRepository::aRecurso); }
    /** Incluye los eliminados: lo usan las solicitudes y préstamos antiguos para conservar su historial. */
    public Optional<Recurso> buscarConBajas(long id) { return tabla.buscar(id).map(RecursoRepository::aRecurso); }
    public List<Recurso> todos() {
        List<Recurso> r = new ArrayList<>();
        for (RecursoFila f : tabla.todas()) if (f.activo()) r.add(aRecurso(f));
        return r;
    }
    /** Todos los recursos por id, incluidos los eliminados (una sola consulta; lo usan los listados de solicitudes). */
    public Map<Long, Recurso> todosConBajasPorId() {
        Map<Long, Recurso> m = new HashMap<>(); for (RecursoFila f : tabla.todas()) m.put(f.id(), aRecurso(f)); return m;
    }
    public void eliminar(long id) {
        tabla.buscar(id).ifPresent(f -> tabla.guardar(new RecursoFila(f.id(), f.tipo(), f.nombre(), f.estado(), f.ubicacion(), f.capacidad(),
            f.marca(), f.modelo(), f.numeroSerie(), false)));
    }

    private static Recurso aRecurso(RecursoFila f) {
        Recurso r = TipoRecurso.valueOf(f.tipo()) == TipoRecurso.LABORATORIO
            ? new Laboratorio(f.nombre(), f.ubicacion(), f.capacidad() == null ? 0 : f.capacidad())
            : new Equipo(f.nombre(), f.marca(), f.modelo(), f.numeroSerie());
        r.setId(f.id()); r.restaurarEstado(EstadoRecurso.valueOf(f.estado()));
        return r;
    }
}
