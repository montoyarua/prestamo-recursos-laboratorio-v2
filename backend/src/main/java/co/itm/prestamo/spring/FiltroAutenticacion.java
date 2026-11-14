package co.itm.prestamo.spring;

import co.itm.prestamo.dominio.Usuario;
import co.itm.prestamo.usuarios.SesionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Componente «Autenticación» del C4: lee el token «Authorization: Bearer», identifica al usuario y lo deja disponible para los
 * controladores. Solo POST /api/sesion (iniciar sesión) es pública; el resto responde 401 sin sesión.
 */
@Component
public class FiltroAutenticacion extends OncePerRequestFilter {
    private final SesionService sesiones;
    public FiltroAutenticacion(SesionService sesiones) { this.sesiones = sesiones; }

    @Override protected boolean shouldNotFilter(HttpServletRequest peticion) { return !peticion.getRequestURI().startsWith("/api/"); }

    @Override protected void doFilterInternal(HttpServletRequest peticion, HttpServletResponse respuesta, FilterChain cadena)
            throws ServletException, IOException {
        String cabecera = peticion.getHeader("Authorization");
        String token = (cabecera != null && cabecera.startsWith("Bearer ")) ? cabecera.substring(7).trim() : null;
        Optional<Usuario> actor = sesiones.usuarioDe(token);
        boolean publica = peticion.getMethod().equals("POST") && peticion.getRequestURI().equals("/api/sesion");
        if (actor.isEmpty() && !publica) {
            respuesta.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            respuesta.setContentType("application/json;charset=UTF-8");
            respuesta.getWriter().write("{\"error\":\"Debe iniciar sesión para continuar\"}");
            return;
        }
        actor.ifPresent(u -> peticion.setAttribute("actor", u));
        if (token != null) peticion.setAttribute("token", token);
        cadena.doFilter(peticion, respuesta);
    }
}
