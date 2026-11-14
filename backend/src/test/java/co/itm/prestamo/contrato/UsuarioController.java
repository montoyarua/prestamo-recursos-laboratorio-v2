package co.itm.prestamo.contrato;

import co.itm.prestamo.api.Vistas;
import co.itm.prestamo.api.Convertir;

import co.itm.prestamo.excepciones.RecursoNoEncontradoException;
import co.itm.prestamo.usuarios.UsuarioService;
import java.util.Map;

/** HU-10: GET /api/usuarios (listar) y POST /api/usuarios (crear con rol). Solo el encargado. */
public class UsuarioController implements Controlador {
    private final UsuarioService usuarios;
    public UsuarioController(UsuarioService usuarios) { this.usuarios = usuarios; }
    @Override public Object manejar(Peticion p) {
        if (p.es("GET", 0)) return Vistas.lista(usuarios.listarUsuarios(p.actor()), Vistas::usuario);
        if (p.es("POST", 0)) {
            Map<String, String> d = p.cuerpo();
            return Vistas.usuario(usuarios.registrarUsuario(p.actor(), d.get("nombre"), d.get("correo"), d.get("contrasena"), d.get("rol")));
        }
        throw new RecursoNoEncontradoException("Ruta no encontrada");
    }
}
