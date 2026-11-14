package co.itm.prestamo.spring;

import co.itm.prestamo.api.Convertir;
import co.itm.prestamo.api.Vistas;
import co.itm.prestamo.dominio.Usuario;
import co.itm.prestamo.excepciones.CamposIncompletosException;
import co.itm.prestamo.solicitudes.NuevaSolicitud;
import co.itm.prestamo.solicitudes.SolicitudOperaciones;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

/** HU-02, HU-03, HU-07: POST /api/solicitudes, GET /pendientes, GET /mias, POST /{id}/aprobar, POST /{id}/rechazar. */
@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudesRest {
    private final SolicitudOperaciones solicitudes;
    public SolicitudesRest(SolicitudOperaciones solicitudes) { this.solicitudes = solicitudes; }

    @PostMapping
    public Map<String, Object> registrar(@RequestAttribute("actor") Usuario actor, @RequestBody(required = false) Map<String, Object> cuerpo) {
        Map<String, String> d = Cuerpo.textos(cuerpo);
        Long recurso = null;
        if (d.get("recursoId") != null && !d.get("recursoId").isBlank()) {
            try { recurso = Long.parseLong(d.get("recursoId").trim()); }
            catch (NumberFormatException e) { throw new CamposIncompletosException("Recurso no válido"); }
        }
        return Vistas.solicitud(solicitudes.registrarSolicitud(actor,
            new NuevaSolicitud(recurso, Convertir.fecha(d.get("fecha")), Convertir.hora(d.get("horaInicio")), Convertir.hora(d.get("horaFin")), d.get("motivo"))));
    }

    @GetMapping("/pendientes")
    public List<Object> pendientes(@RequestAttribute("actor") Usuario actor) { return Vistas.lista(solicitudes.listarPendientes(actor), Vistas::solicitud); }

    @GetMapping("/mias")
    public List<Object> mias(@RequestAttribute("actor") Usuario actor) { return Vistas.lista(solicitudes.listarDeUsuario(actor), Vistas::solicitud); }

    @PostMapping("/{id}/aprobar")
    public Map<String, Object> aprobar(@RequestAttribute("actor") Usuario actor, @PathVariable long id) { return Vistas.solicitud(solicitudes.aprobarSolicitud(actor, id)); }

    @PostMapping("/{id}/rechazar")
    public Map<String, Object> rechazar(@RequestAttribute("actor") Usuario actor, @PathVariable long id) { return Vistas.solicitud(solicitudes.rechazarSolicitud(actor, id)); }
}
