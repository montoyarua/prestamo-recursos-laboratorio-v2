package co.itm.prestamo.prestamos;

import co.itm.prestamo.dominio.*;
import co.itm.prestamo.excepciones.RecursoNoEncontradoException;
import co.itm.prestamo.puertos.PrestamoPort;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import co.itm.prestamo.inventario.RecursoRepository;

/** Gestión de Préstamos: crea el préstamo al aprobarse la solicitud (HU-07) y lo cierra con la devolución (HU-08). */
public class PrestamoService implements PrestamoPort, PrestamoOperaciones {
    private final PrestamoRepository prestamos; private final RecursoRepository recursos; private final Clock reloj;
    public PrestamoService(PrestamoRepository prestamos, RecursoRepository recursos, Clock reloj) { this.prestamos = prestamos; this.recursos = recursos; this.reloj = reloj; }

    @Override public synchronized void crearPrestamo(Solicitud s) {
        prestamos.guardar(new Prestamo(s, LocalDateTime.now(reloj)));
        Recurso r = s.getRecurso(); r.marcarPrestado(); recursos.guardar(r);
    }
    @Override public synchronized Prestamo registrarDevolucion(Usuario actor, long id) {
        Prestamo p = prestamos.buscar(id).orElseThrow(() -> new RecursoNoEncontradoException("El préstamo no existe"));
        p.finalizar(LocalDateTime.now(reloj));           // lanza si ya estaba «Finalizado»
        prestamos.guardar(p);
        Recurso r = p.getSolicitud().getRecurso();
        if (!tienePrestamoActivo(r.getId())) { r.marcarDisponible(); recursos.guardar(r); }
        return p;
    }
    @Override public synchronized List<Prestamo> listarPrestamosActivos(Usuario actor) {
        List<Prestamo> r = new ArrayList<>(); for (Prestamo p : prestamos.todos()) if (p.estaActivo()) r.add(p); return r;
    }
    @Override public synchronized List<Prestamo> listarPrestamosUsuario(Usuario actor) {
        List<Prestamo> r = new ArrayList<>();
        for (Prestamo p : prestamos.todos()) if (p.getSolicitud().getSolicitante().getId() == actor.getId()) r.add(p);
        return r;
    }
    public synchronized boolean tienePrestamoActivo(long recursoId) {
        for (Prestamo p : prestamos.todos()) if (p.estaActivo() && p.getSolicitud().getRecurso().getId() == recursoId) return true;
        return false;
    }
    public synchronized List<Prestamo> activosDeRecurso(long recursoId) {
        List<Prestamo> r = new ArrayList<>();
        for (Prestamo p : prestamos.todos()) if (p.estaActivo() && p.getSolicitud().getRecurso().getId() == recursoId) r.add(p);
        return r;
    }
    /** Ids de las solicitudes cuyo préstamo ya se devolvió (una sola lectura de préstamos). */
    public synchronized Set<Long> solicitudesDevueltas() {
        Set<Long> r = new HashSet<>(); for (Prestamo p : prestamos.todos()) if (!p.estaActivo()) r.add(p.getSolicitud().getId()); return r;
    }
    public synchronized List<Prestamo> todosActivos() {
        List<Prestamo> r = new ArrayList<>(); for (Prestamo p : prestamos.todos()) if (p.estaActivo()) r.add(p); return r;
    }
    /** ¿La solicitud aprobada ya terminó con la devolución? Entonces deja de bloquear al recurso y al usuario. */
    public synchronized boolean estaDevuelta(long solicitudId) {
        for (Prestamo p : prestamos.todos()) if (p.getSolicitud().getId() == solicitudId) return !p.estaActivo();
        return false;
    }
}
