package co.itm.prestamo.contrato;

import co.itm.prestamo.api.Vistas;
import co.itm.prestamo.api.Convertir;

import co.itm.prestamo.excepciones.*;
import co.itm.prestamo.inventario.RecursoService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

/** HU-06 (inventario) y HU-01 (disponibilidad): /api/recursos, /api/recursos/disponibles, /api/recursos/{id}. */
public class RecursoController implements Controlador {
    private final RecursoService recursos;
    public RecursoController(RecursoService recursos) { this.recursos = recursos; }
    @Override public Object manejar(Peticion p) {
        if (p.es("GET", 0)) return Vistas.lista(recursos.consultarRecursos(), Vistas::recurso);
        if (p.es("GET", "disponibles")) {
            Map<String, String> q = p.consulta();
            return Vistas.lista(recursos.consultarDisponibles(Convertir.fecha(q.get("fecha")), Convertir.hora(q.get("horaInicio")), Convertir.hora(q.get("horaFin"))), Vistas::recurso);
        }
        if (p.es("POST", 0)) return Vistas.recurso(recursos.registrarRecurso(p.actor(), p.cuerpo().get("tipo"), p.cuerpo()));
        if (p.es("PUT", 1)) return Vistas.recurso(recursos.modificarRecurso(p.actor(), p.idEn(0), p.cuerpo()));
        if (p.es("DELETE", 1)) { recursos.eliminarRecurso(p.actor(), p.idEn(0)); return Map.of("ok", true); }
        throw new RecursoNoEncontradoException("Ruta no encontrada");
    }
}
