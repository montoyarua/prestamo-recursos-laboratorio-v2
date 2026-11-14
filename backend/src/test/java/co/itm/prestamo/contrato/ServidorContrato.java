package co.itm.prestamo.contrato;

import co.itm.prestamo.Main;
import co.itm.prestamo.Main.Sistema;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Servidor de CONTRATO, solo para pruebas (no se entrega como aplicación): expone con el servidor HTTP del JDK, sin Spring y con datos
 * en memoria, las mismas rutas REST que la aplicación Spring Boot. Sirve para probar el contrato de la API (PruebasApi) y la interfaz React
 * sin necesitar PostgreSQL ni Maven.
 */
public class ServidorContrato {
    public static Map<String, Controlador> controladores(Sistema s) {
        Map<String, Controlador> c = new LinkedHashMap<>();
        c.put("sesion", new SesionController(s.usuarios(), s.sesiones()));
        c.put("usuarios", new UsuarioController(s.usuarios()));
        c.put("recursos", new RecursoController(s.recursos()));
        c.put("solicitudes", new SolicitudController(s.solicitudes()));
        c.put("prestamos", new PrestamoController(s.prestamos()));
        c.put("notificaciones", new NotificacionController(s.notificaciones()));
        return c;
    }

    /** Uso: java ServidorContrato [puerto] [carpeta de la interfaz compilada] */
    public static void main(String[] args) throws Exception {
        int puerto = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        Path web = Path.of(args.length > 1 ? args[1] : "backend/src/main/resources/static");
        Sistema s = Armado.enMemoria(java.time.Clock.systemDefaultZone());
        new ServidorHttp(controladores(s), s.sesiones(), web).iniciar(puerto);
        System.out.println("Servidor de contrato (solo pruebas) en http://localhost:" + puerto);
    }
}
