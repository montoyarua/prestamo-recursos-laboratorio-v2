package co.itm.prestamo.solicitudes;

import co.itm.prestamo.dominio.*;
import co.itm.prestamo.inventario.RecursoRepository;
import co.itm.prestamo.persistencia.Filas.SolicitudFila;
import co.itm.prestamo.persistencia.Tabla;
import co.itm.prestamo.usuarios.UsuarioRepository;
import java.util.*;

/** Repositorio de solicitudes: reconstruye cada solicitud con su solicitante, su recurso y su estado (patrón State). */
public class SolicitudRepository {
    private final Tabla<SolicitudFila> tabla; private final UsuarioRepository usuarios; private final RecursoRepository recursos;
    public SolicitudRepository(Tabla<SolicitudFila> tabla, UsuarioRepository usuarios, RecursoRepository recursos) {
        this.tabla = tabla; this.usuarios = usuarios; this.recursos = recursos;
    }
    public Solicitud guardar(Solicitud s) {
        SolicitudFila f = tabla.guardar(new SolicitudFila(s.getId(), s.getSolicitante().getId(), s.getRecurso().getId(), s.getFecha(),
            s.getHoraInicio(), s.getHoraFin(), s.getMotivo(), s.getEstado().name(), s.getFechaRegistro()));
        s.setId(f.id()); return s;
    }
    public Optional<Solicitud> buscar(long id) { return tabla.buscar(id).map(this::aSolicitud); }
    /** Tres consultas en total (solicitudes, usuarios, recursos), sin importar cuántas solicitudes haya. */
    public List<Solicitud> todas() {
        Map<Long, Usuario> us = usuarios.todosPorId(); Map<Long, Recurso> rs = recursos.todosConBajasPorId();
        List<Solicitud> r = new ArrayList<>();
        for (SolicitudFila f : tabla.todas()) r.add(aSolicitud(f, us.get(f.usuarioId()), rs.get(f.recursoId())));
        return r;
    }

    private Solicitud aSolicitud(SolicitudFila f) {
        return aSolicitud(f, usuarios.buscar(f.usuarioId()).orElse(null), recursos.buscarConBajas(f.recursoId()).orElse(null));
    }
    private Solicitud aSolicitud(SolicitudFila f, Usuario u, Recurso r) {
        if (u == null) throw new IllegalStateException("Solicitud sin usuario: " + f.id());
        if (r == null) throw new IllegalStateException("Solicitud sin recurso: " + f.id());
        return Solicitud.restaurar(f.id(), u, r, f.fecha(), f.horaInicio(), f.horaFin(), f.motivo(), f.fechaRegistro(), EstadoSolicitud.valueOf(f.estado()));
    }
}
