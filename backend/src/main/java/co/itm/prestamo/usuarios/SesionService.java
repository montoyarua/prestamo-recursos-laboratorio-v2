package co.itm.prestamo.usuarios;

import co.itm.prestamo.dominio.Usuario;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** Autenticación: sesiones en memoria identificadas por un token aleatorio (el filtro de la API lo valida en cada petición). */
public class SesionService {
    private final Map<String, Usuario> sesiones = new ConcurrentHashMap<>();
    private final SecureRandom azar = new SecureRandom();
    public String abrir(Usuario u) {
        byte[] b = new byte[24]; azar.nextBytes(b);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(b); sesiones.put(token, u); return token;
    }
    public Optional<Usuario> usuarioDe(String token) { return token == null ? Optional.empty() : Optional.ofNullable(sesiones.get(token)); }
    public void cerrar(String token) { if (token != null) sesiones.remove(token); }
}
