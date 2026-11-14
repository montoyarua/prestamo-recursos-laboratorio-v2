package co.itm.prestamo.spring;

import co.itm.prestamo.api.Vistas;
import co.itm.prestamo.dominio.Usuario;
import co.itm.prestamo.prestamos.PrestamoOperaciones;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

/** HU-05 y HU-08: GET /api/prestamos/mios, GET /api/prestamos/activos, POST /api/prestamos/{id}/devolver. */
@RestController
@RequestMapping("/api/prestamos")
public class PrestamosRest {
    private final PrestamoOperaciones prestamos;
    public PrestamosRest(PrestamoOperaciones prestamos) { this.prestamos = prestamos; }

    @GetMapping("/mios")
    public List<Object> mios(@RequestAttribute("actor") Usuario actor) { return Vistas.lista(prestamos.listarPrestamosUsuario(actor), Vistas::prestamo); }

    @GetMapping("/activos")
    public List<Object> activos(@RequestAttribute("actor") Usuario actor) { return Vistas.lista(prestamos.listarPrestamosActivos(actor), Vistas::prestamo); }

    @PostMapping("/{id}/devolver")
    public Map<String, Object> devolver(@RequestAttribute("actor") Usuario actor, @PathVariable long id) { return Vistas.prestamo(prestamos.registrarDevolucion(actor, id)); }
}
