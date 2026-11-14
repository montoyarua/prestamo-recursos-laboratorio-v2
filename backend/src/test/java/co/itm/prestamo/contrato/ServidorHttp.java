package co.itm.prestamo.contrato;

import co.itm.prestamo.api.Vistas;
import co.itm.prestamo.api.Convertir;

import co.itm.prestamo.dominio.Usuario;
import co.itm.prestamo.excepciones.*;
import co.itm.prestamo.usuarios.SesionService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Backend/API sin frameworks (servidor HTTP del JDK). Hace de filtro de Autenticación: lee el token «Authorization: Bearer»,
 * identifica al usuario y entrega la petición al controlador del recurso. Solo POST /api/sesion es pública.
 */
public class ServidorHttp {
    private final Map<String, Controlador> controladores; private final SesionService sesiones; private final Path carpetaWeb;
    public ServidorHttp(Map<String, Controlador> controladores, SesionService sesiones, Path carpetaWeb) {
        this.controladores = controladores; this.sesiones = sesiones; this.carpetaWeb = carpetaWeb;
    }

    public HttpServer iniciar(int puerto) throws IOException {
        HttpServer srv = HttpServer.create(new InetSocketAddress(puerto), 0);
        srv.createContext("/api/", this::api);
        srv.createContext("/", this::estatico);
        srv.start(); return srv;
    }

    private void estatico(HttpExchange ex) throws IOException {
        String ruta = ex.getRequestURI().getPath(); if (ruta.equals("/")) ruta = "/index.html";
        Path p = carpetaWeb.resolve(ruta.substring(1)).normalize();
        if (!p.startsWith(carpetaWeb.normalize()) || !Files.isRegularFile(p)) { responder(ex, 404, "{\"error\":\"No encontrado\"}"); return; }
        String n = p.getFileName().toString();
        String tipo = n.endsWith(".html") ? "text/html; charset=utf-8" : n.endsWith(".js") ? "text/javascript; charset=utf-8"
            : n.endsWith(".css") ? "text/css; charset=utf-8" : n.endsWith(".svg") ? "image/svg+xml" : "application/octet-stream";
        ex.getResponseHeaders().set("Content-Type", tipo);
        byte[] b = Files.readAllBytes(p); ex.sendResponseHeaders(200, b.length);
        try (OutputStream o = ex.getResponseBody()) { o.write(b); }
    }

    private void api(HttpExchange ex) throws IOException {
        try {
            String metodo = ex.getRequestMethod();
            String[] ruta = ex.getRequestURI().getPath().substring(5).split("/");   // quita «/api/»
            String cuerpo = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            String auth = ex.getRequestHeaders().getFirst("Authorization");
            String token = (auth != null && auth.startsWith("Bearer ")) ? auth.substring(7).trim() : null;
            Usuario actor = sesiones.usuarioDe(token).orElse(null);
            boolean publica = ruta[0].equals("sesion") && metodo.equals("POST");
            if (actor == null && !publica) throw new NoAutenticadoException("Debe iniciar sesión para continuar");
            Controlador c = controladores.get(ruta[0]);
            if (c == null) throw new RecursoNoEncontradoException("Ruta no encontrada");
            Peticion p = new Peticion(metodo, Arrays.asList(ruta).subList(1, ruta.length), consulta(ex.getRequestURI().getRawQuery()), Json.leerPlano(cuerpo), actor, token);
            responder(ex, 200, Json.escribir(c.manejar(p)));
        } catch (NegocioException e) {
            responder(ex, e.getEstadoHttp(), Json.escribir(Map.of("error", e.getMessage())));
        } catch (Exception e) {
            e.printStackTrace(); responder(ex, 500, Json.escribir(Map.of("error", "Error interno")));
        }
    }

    private static Map<String, String> consulta(String q) {
        Map<String, String> m = new HashMap<>();
        if (q == null) return m;
        for (String par : q.split("&")) {
            int i = par.indexOf('=');
            if (i > 0) m.put(URLDecoder.decode(par.substring(0, i), StandardCharsets.UTF_8), URLDecoder.decode(par.substring(i + 1), StandardCharsets.UTF_8));
        }
        return m;
    }

    private void responder(HttpExchange ex, int codigo, String texto) throws IOException {
        byte[] b = texto.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(codigo, b.length);
        try (OutputStream o = ex.getResponseBody()) { o.write(b); }
    }
}
