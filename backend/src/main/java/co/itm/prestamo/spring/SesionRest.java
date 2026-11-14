package co.itm.prestamo.spring;

import co.itm.prestamo.api.Vistas;
import co.itm.prestamo.dominio.Usuario;
import co.itm.prestamo.usuarios.SesionService;
import co.itm.prestamo.usuarios.UsuarioService;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

/** HU-09: POST /api/sesion inicia sesión (pública); DELETE /api/sesion la cierra. */
@RestController
@RequestMapping("/api/sesion")
public class SesionRest {
    private final UsuarioService usuarios; private final SesionService sesiones;
    public SesionRest(UsuarioService usuarios, SesionService sesiones) { this.usuarios = usuarios; this.sesiones = sesiones; }

    @PostMapping
    public Map<String, Object> iniciar(@RequestBody(required = false) Map<String, Object> cuerpo) {
        Map<String, String> d = Cuerpo.textos(cuerpo);
        Usuario u = usuarios.autenticar(d.get("correo"), d.get("contrasena"));
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("token", sesiones.abrir(u)); m.put("usuario", Vistas.usuario(u));
        return m;
    }

    @DeleteMapping
    public Map<String, Object> cerrar(@RequestAttribute(name = "token", required = false) String token) {
        sesiones.cerrar(token);
        return Map.of("ok", true);
    }
}
