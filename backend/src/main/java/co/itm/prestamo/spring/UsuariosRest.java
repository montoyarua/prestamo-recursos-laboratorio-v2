package co.itm.prestamo.spring;

import co.itm.prestamo.api.Vistas;
import co.itm.prestamo.dominio.Usuario;
import co.itm.prestamo.usuarios.UsuarioService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

/** HU-10: GET /api/usuarios (listar) y POST /api/usuarios (crear con rol). Solo el encargado. */
@RestController
@RequestMapping("/api/usuarios")
public class UsuariosRest {
    private final UsuarioService usuarios;
    public UsuariosRest(UsuarioService usuarios) { this.usuarios = usuarios; }

    @GetMapping
    public List<Object> listar(@RequestAttribute("actor") Usuario actor) { return Vistas.lista(usuarios.listarUsuarios(actor), Vistas::usuario); }

    @PostMapping
    public Map<String, Object> crear(@RequestAttribute("actor") Usuario actor, @RequestBody(required = false) Map<String, Object> cuerpo) {
        Map<String, String> d = Cuerpo.textos(cuerpo);
        return Vistas.usuario(usuarios.registrarUsuario(actor, d.get("nombre"), d.get("correo"), d.get("contrasena"), d.get("rol")));
    }
}
