package co.itm.prestamo.spring;

import co.itm.prestamo.api.Convertir;
import co.itm.prestamo.api.Vistas;
import co.itm.prestamo.dominio.Usuario;
import co.itm.prestamo.inventario.RecursoService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

/** HU-06 (inventario) y HU-01 (disponibilidad): /api/recursos, /api/recursos/disponibles, /api/recursos/{id}. */
@RestController
@RequestMapping("/api/recursos")
public class RecursosRest {
    private final RecursoService recursos;
    public RecursosRest(RecursoService recursos) { this.recursos = recursos; }

    @GetMapping
    public List<Object> listar() { return Vistas.lista(recursos.consultarRecursos(), Vistas::recurso); }

    @GetMapping("/disponibles")
    public List<Object> disponibles(@RequestParam(required = false) String fecha, @RequestParam(required = false) String horaInicio,
                                    @RequestParam(required = false) String horaFin) {
        return Vistas.lista(recursos.consultarDisponibles(Convertir.fecha(fecha), Convertir.hora(horaInicio), Convertir.hora(horaFin)), Vistas::recurso);
    }

    @PostMapping
    public Map<String, Object> registrar(@RequestAttribute("actor") Usuario actor, @RequestBody(required = false) Map<String, Object> cuerpo) {
        Map<String, String> d = Cuerpo.textos(cuerpo);
        return Vistas.recurso(recursos.registrarRecurso(actor, d.get("tipo"), d));
    }

    @PutMapping("/{id}")
    public Map<String, Object> modificar(@RequestAttribute("actor") Usuario actor, @PathVariable long id,
                                         @RequestBody(required = false) Map<String, Object> cuerpo) {
        return Vistas.recurso(recursos.modificarRecurso(actor, id, Cuerpo.textos(cuerpo)));
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> eliminar(@RequestAttribute("actor") Usuario actor, @PathVariable long id) {
        recursos.eliminarRecurso(actor, id);
        return Map.of("ok", true);
    }
}
