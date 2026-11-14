package co.itm.prestamo.usuarios;

import co.itm.prestamo.dominio.*;
import co.itm.prestamo.excepciones.*;
import co.itm.prestamo.puertos.*;
import java.util.*;

/** Gestión de Usuarios: autenticación (HU-09), alta de cuentas con rol (HU-10, RN-07) y permisos por rol (RN-02). */
public class UsuarioService implements AutorizacionPort {
    private final UsuarioRepository usuarios;
    private final Atomicidad atomicidad;
    public UsuarioService(UsuarioRepository usuarios, Atomicidad atomicidad) { this.usuarios = usuarios; this.atomicidad = atomicidad; }

    /** HU-09. Devuelve el usuario si las credenciales son correctas. */
    public synchronized Usuario autenticar(String correo, String contrasena) {
        List<String> faltan = new ArrayList<>();
        if (vacio(correo)) faltan.add("correo"); if (vacio(contrasena)) faltan.add("contraseña");
        if (!faltan.isEmpty()) throw new CamposIncompletosException("Faltan campos obligatorios: " + String.join(", ", faltan));
        Usuario u = buscarPorCorreo(correo).orElse(null);
        if (u == null || !Contrasenas.coincide(contrasena, u.getContrasena()))
            throw new CredencialesInvalidasException("Usuario o contraseña incorrectos");
        return u;
    }

    /** HU-10. Solo el encargado crea cuentas (Proxy de autorización aplicado en el propio servicio). */
    public Usuario registrarUsuario(Usuario actor, String nombre, String correo, String contrasena, String rol) {
        return atomicidad.ejecutar(() -> registrar(actor, nombre, correo, contrasena, rol));   // RN-07: comprobar y guardar juntos
    }
    private synchronized Usuario registrar(Usuario actor, String nombre, String correo, String contrasena, String rol) {
        if (actor == null || !tienePermiso(actor, Operacion.GESTIONAR_USUARIOS))
            throw new AccesoDenegadoException("Su rol no tiene permiso para administrar usuarios");
        List<String> faltan = new ArrayList<>();
        if (vacio(nombre)) faltan.add("nombre"); if (vacio(correo)) faltan.add("correo");
        if (vacio(contrasena)) faltan.add("contraseña"); if (vacio(rol)) faltan.add("rol");
        if (!faltan.isEmpty()) throw new CamposIncompletosException("Faltan datos obligatorios: " + String.join(", ", faltan));
        RolUsuario r;
        try { r = RolUsuario.valueOf(rol.trim().toUpperCase()); } catch (IllegalArgumentException e) { throw new CamposIncompletosException("Faltan datos obligatorios: rol válido"); }
        if (existeCorreo(correo)) throw new CorreoDuplicadoException("Ya existe una cuenta con ese correo");
        return agregar(nombre.trim(), correo.trim(), contrasena, r);
    }

    /** Alta sin actor, solo para cargar las cuentas iniciales del sistema (arranque). */
    public synchronized Usuario crearCuentaInicial(String nombre, String correo, String contrasena, RolUsuario rol) { return agregar(nombre, correo, contrasena, rol); }

    public synchronized boolean existeCorreo(String correo) { return !vacio(correo) && buscarPorCorreo(correo).isPresent(); }
    public synchronized Optional<Usuario> buscar(long id) { return usuarios.buscar(id); }
    public synchronized List<Usuario> listarUsuarios(Usuario actor) {
        if (actor == null || !tienePermiso(actor, Operacion.GESTIONAR_USUARIOS)) throw new AccesoDenegadoException("Su rol no tiene permiso para administrar usuarios");
        return usuarios.todos();
    }
    /** RN-02: el encargado decide, devuelve y administra; el estudiante y el docente solo piden préstamos (HU-02). */
    @Override public boolean tienePermiso(Usuario u, Operacion op) {
        if (u.getRol() == RolUsuario.ENCARGADO) return op != Operacion.REGISTRAR_SOLICITUD;
        return op == Operacion.REGISTRAR_SOLICITUD;
    }

    private Usuario agregar(String nombre, String correo, String contrasena, RolUsuario rol) {
        return usuarios.guardar(new Usuario(0, nombre, correo, Contrasenas.cifrar(contrasena), rol));
    }
    private Optional<Usuario> buscarPorCorreo(String correo) { return usuarios.buscarPorCorreo(correo); }
    public synchronized boolean hayUsuarios() { return usuarios.hayUsuarios(); }
    private static boolean vacio(String t) { return t == null || t.isBlank(); }
}
