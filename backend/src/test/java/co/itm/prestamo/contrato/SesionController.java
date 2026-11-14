package co.itm.prestamo.contrato;

import co.itm.prestamo.api.Vistas;
import co.itm.prestamo.api.Convertir;

import co.itm.prestamo.dominio.Usuario;
import co.itm.prestamo.excepciones.RecursoNoEncontradoException;
import co.itm.prestamo.usuarios.SesionService;
import co.itm.prestamo.usuarios.UsuarioService;
import java.util.*;

/** HU-09: POST /api/sesion inicia sesión; DELETE /api/sesion la cierra. */
public class SesionController implements Controlador {
    private final UsuarioService usuarios; private final SesionService sesiones;
    public SesionController(UsuarioService usuarios, SesionService sesiones) { this.usuarios = usuarios; this.sesiones = sesiones; }
    @Override public Object manejar(Peticion p) {
        if (p.es("POST", 0)) {
            Usuario u = usuarios.autenticar(p.cuerpo().get("correo"), p.cuerpo().get("contrasena"));
            Map<String, Object> m = new LinkedHashMap<>(); m.put("token", sesiones.abrir(u)); m.put("usuario", Vistas.usuario(u)); return m;
        }
        if (p.es("DELETE", 0)) { sesiones.cerrar(p.token()); return Map.of("ok", true); }
        throw new RecursoNoEncontradoException("Ruta no encontrada");
    }
}
