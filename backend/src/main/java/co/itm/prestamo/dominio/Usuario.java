package co.itm.prestamo.dominio;

/** Cuenta del sistema. {@code contrasena} guarda siempre el resumen cifrado (RNF-06), nunca el texto original. */
public class Usuario {
    private final long id; private final String nombre; private final String correo;
    private final String contrasena; private final RolUsuario rol;
    public Usuario(long id, String nombre, String correo, String contrasena, RolUsuario rol) {
        this.id = id; this.nombre = nombre; this.correo = correo; this.contrasena = contrasena; this.rol = rol;
    }
    public long getId() { return id; } public String getNombre() { return nombre; }
    public String getCorreo() { return correo; } public String getContrasena() { return contrasena; }
    public RolUsuario getRol() { return rol; }
}
