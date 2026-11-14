package co.itm.prestamo.puertos;

import co.itm.prestamo.dominio.Usuario;

/** Puerto hacia Gestión de Usuarios: ¿el rol del usuario puede ejecutar la operación? (RN-02) */
public interface AutorizacionPort { boolean tienePermiso(Usuario usuario, Operacion operacion); }
