package co.itm.prestamo.spring;

import co.itm.prestamo.api.Vistas;
import co.itm.prestamo.dominio.Usuario;
import co.itm.prestamo.prestamos.NotificacionService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

/** HU-04: GET /api/notificaciones y POST /api/notificaciones/leidas. */
@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionesRest {
    private final NotificacionService notificaciones;
    public NotificacionesRest(NotificacionService notificaciones) { this.notificaciones = notificaciones; }

    @GetMapping
    public List<Object> listar(@RequestAttribute("actor") Usuario actor) { return Vistas.lista(notificaciones.deUsuario(actor.getId()), Vistas::notificacion); }

    @PostMapping("/leidas")
    public Map<String, Object> marcarLeidas(@RequestAttribute("actor") Usuario actor) {
        notificaciones.marcarLeidas(actor.getId());
        return Map.of("ok", true);
    }
}
