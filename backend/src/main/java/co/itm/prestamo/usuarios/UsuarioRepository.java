package co.itm.prestamo.usuarios;

import co.itm.prestamo.dominio.*;
import co.itm.prestamo.persistencia.Filas.UsuarioFila;
import co.itm.prestamo.persistencia.Tabla;
import java.util.*;

/** Repositorio de usuarios: traduce entre el objeto Usuario y su fila en la tabla. */
public class UsuarioRepository {
    private final Tabla<UsuarioFila> tabla;
    public UsuarioRepository(Tabla<UsuarioFila> tabla) { this.tabla = tabla; }

    public Usuario guardar(Usuario u) {
        UsuarioFila f = tabla.guardar(new UsuarioFila(u.getId(), u.getNombre(), u.getCorreo(), u.getContrasena(), u.getRol().name()));
        return aUsuario(f);
    }
    public Optional<Usuario> buscar(long id) { return tabla.buscar(id).map(UsuarioRepository::aUsuario); }
    public Optional<Usuario> buscarPorCorreo(String correo) {
        for (UsuarioFila f : tabla.todas()) if (f.correo().equalsIgnoreCase(correo.trim())) return Optional.of(aUsuario(f));
        return Optional.empty();
    }
    public List<Usuario> todos() { List<Usuario> r = new ArrayList<>(); for (UsuarioFila f : tabla.todas()) r.add(aUsuario(f)); return r; }
    /** Todos los usuarios por id (una sola consulta; lo usan los listados de solicitudes). */
    public Map<Long, Usuario> todosPorId() { Map<Long, Usuario> m = new HashMap<>(); for (Usuario u : todos()) m.put(u.getId(), u); return m; }
    public boolean hayUsuarios() { return !tabla.todas().isEmpty(); }

    private static Usuario aUsuario(UsuarioFila f) { return new Usuario(f.id(), f.nombre(), f.correo(), f.contrasena(), RolUsuario.valueOf(f.rol())); }
}
