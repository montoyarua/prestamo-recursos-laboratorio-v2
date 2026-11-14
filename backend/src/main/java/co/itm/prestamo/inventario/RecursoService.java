package co.itm.prestamo.inventario;

import co.itm.prestamo.dominio.Recurso;
import co.itm.prestamo.dominio.TipoRecurso;
import co.itm.prestamo.dominio.Usuario;
import co.itm.prestamo.excepciones.*;
import co.itm.prestamo.puertos.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/** HU-06 y HU-01. Pide el recurso al creador de su tipo (Factory Method); los creadores llegan inyectados. */
public class RecursoService {
    private final RecursoRepository repositorio;
    private final RecursosVigentesPort vigentes;
    private final DisponibilidadPort disponibilidad;
    private final AutorizacionPort autorizacion;
    private final Map<TipoRecurso, RecursoCreator> creadores;
    private final Atomicidad atomicidad;

    public RecursoService(RecursoRepository repositorio, RecursosVigentesPort vigentes, DisponibilidadPort disponibilidad,
                          AutorizacionPort autorizacion, Map<TipoRecurso, RecursoCreator> creadores, Atomicidad atomicidad) {
        this.repositorio = repositorio; this.vigentes = vigentes; this.disponibilidad = disponibilidad;
        this.autorizacion = autorizacion; this.creadores = new EnumMap<>(creadores); this.atomicidad = atomicidad;
    }
    private void exigirEncargado(Usuario actor) {
        if (actor == null || !autorizacion.tienePermiso(actor, Operacion.GESTIONAR_INVENTARIO))
            throw new AccesoDenegadoException("Su rol no tiene permiso para administrar el inventario");
    }
    public Recurso registrarRecurso(Usuario actor, String tipo, Map<String, String> datos) {
        exigirEncargado(actor);
        TipoRecurso t;
        try { t = TipoRecurso.valueOf(tipo == null ? "" : tipo.trim().toUpperCase()); }
        catch (IllegalArgumentException e) { throw new CamposIncompletosException("Tipo de recurso no válido"); }
        return atomicidad.ejecutar(() -> repositorio.guardar(creadores.get(t).nuevoRecurso(datos)));   // queda «Disponible»
    }
    public Recurso modificarRecurso(Usuario actor, long id, Map<String, String> datos) {
        exigirEncargado(actor);
        return atomicidad.ejecutar(() -> { Recurso r = buscar(id); r.actualizar(datos); return repositorio.guardar(r); });
    }
    public void eliminarRecurso(Usuario actor, long id) {
        exigirEncargado(actor);
        // RN-06: la comprobación y la baja van juntas, sin que entre en medio una solicitud nueva sobre el recurso
        atomicidad.ejecutar(() -> {
            buscar(id);
            if (vigentes.tieneVigentes(id)) throw new RecursoConSolicitudesVigentesException("No se puede eliminar: el recurso tiene solicitudes o préstamos vigentes");
            repositorio.eliminar(id);
            return null;
        });
    }
    public List<Recurso> consultarRecursos() { return repositorio.todos(); }

    /** HU-01: recursos libres en la fecha y el rango consultados (excluye reservas aprobadas y préstamos sin devolver). */
    public List<Recurso> consultarDisponibles(LocalDate fecha, LocalTime inicio, LocalTime fin) {
        List<String> faltan = new ArrayList<>();
        if (fecha == null) faltan.add("fecha"); if (inicio == null) faltan.add("hora de inicio"); if (fin == null) faltan.add("hora de fin");
        if (!faltan.isEmpty()) throw new CamposIncompletosException("Faltan campos obligatorios: " + String.join(", ", faltan));
        if (!inicio.isBefore(fin)) throw new HorarioInvalidoException("La hora de inicio debe ser anterior a la hora de fin");
        List<Recurso> libres = new ArrayList<>();
        Set<Long> ocupados = disponibilidad.recursosOcupados(fecha, inicio, fin);
        for (Recurso r : repositorio.todos()) if (!ocupados.contains(r.getId())) libres.add(r);
        return libres;
    }
    private Recurso buscar(long id) { return repositorio.buscar(id).orElseThrow(() -> new RecursoNoEncontradoException("El recurso no existe")); }
}
