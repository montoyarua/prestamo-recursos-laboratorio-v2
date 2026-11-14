package co.itm.prestamo;

import static co.itm.prestamo.Verificador.*;

import co.itm.prestamo.contrato.*;
import com.sun.net.httpserver.HttpServer;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.*;
import java.util.regex.*;

/** Pruebas de extremo a extremo por HTTP: sesión, permisos por rol y flujos alternos tal como los ve la interfaz web. */
public class PruebasApi {
    record Resp(int codigo, String cuerpo) { boolean contiene(String t) { return cuerpo.contains(t); } }
    static HttpClient cliente = HttpClient.newHttpClient(); static String base;

    static Resp llamar(String metodo, String ruta, String token, String json) throws Exception {
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(base + ruta)).header("Content-Type", "application/json");
        if (token != null) b.header("Authorization", "Bearer " + token);
        b.method(metodo, json == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8));
        HttpResponse<String> r = cliente.send(b.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return new Resp(r.statusCode(), r.body());
    }
    static String entrar(String correo, String clave) throws Exception {
        Resp r = llamar("POST", "/api/sesion", null, "{\"correo\":\"" + correo + "\",\"contrasena\":\"" + clave + "\"}");
        Matcher m = Pattern.compile("\"token\":\"([^\"]+)\"").matcher(r.cuerpo()); return m.find() ? m.group(1) : null;
    }
    static String solicitud(long recurso, String fecha, String ini, String fin, String motivo) {
        return "{\"recursoId\":\"" + recurso + "\",\"fecha\":\"" + fecha + "\",\"horaInicio\":\"" + ini + "\",\"horaFin\":\"" + fin + "\",\"motivo\":\"" + motivo + "\"}";
    }

    public static void main(String[] args) throws Exception {
        Clock reloj = Clock.fixed(Instant.parse("2026-11-10T13:00:00Z"), ZoneId.of("America/Bogota"));
        Main.Sistema sis = co.itm.prestamo.contrato.Armado.enMemoria(reloj);
        HttpServer srv = new ServidorHttp(ServidorContrato.controladores(sis), sis.sesiones(), Path.of("backend/src/main/resources/static")).iniciar(0);
        base = "http://localhost:" + srv.getAddress().getPort();
        try { pruebas(); } finally { srv.stop(0); }
        resumen("PruebasApi");
        if (fallidas() > 0) System.exit(1);
    }

    static void pruebas() throws Exception {
        titulo("API · HU-09 sesión");
        verificar("Sin sesión se rechaza cualquier consulta (401)", llamar("GET", "/api/recursos", null, null).codigo() == 401);
        verificar("HU-09 Esc.2: credenciales incorrectas → 401 con mensaje", llamar("POST", "/api/sesion", null, "{\"correo\":\"estudiante@itm.edu.co\",\"contrasena\":\"x\"}") .contiene("incorrectos"));
        Resp vacio = llamar("POST", "/api/sesion", null, "{\"correo\":\"\",\"contrasena\":\"\"}");
        verificar("HU-09 Esc.3: campos vacíos → 400 e indica los obligatorios", vacio.codigo() == 400 && vacio.contiene("correo, contraseña"));
        String est = entrar("estudiante@itm.edu.co", "Estudiante123"), enc = entrar("encargado@itm.edu.co", "Encargado123"), doc = entrar("docente@itm.edu.co", "Docente123");
        verificar("HU-09 Esc.1: credenciales correctas → token de sesión", est != null && enc != null && doc != null);
        verificar("La respuesta de la sesión no expone la contraseña", !llamar("POST", "/api/sesion", null, "{\"correo\":\"estudiante@itm.edu.co\",\"contrasena\":\"Estudiante123\"}").contiene("Estudiante123"));
        verificar("Un token inventado no sirve (401)", llamar("GET", "/api/recursos", "token-falso", null).codigo() == 401);

        titulo("API · HU-01 / HU-02 / HU-03");
        verificar("HU-01: el estudiante consulta los recursos disponibles", llamar("GET", "/api/recursos/disponibles?fecha=2026-11-20&horaInicio=08:00&horaFin=10:00", est, null).contiene("Laboratorio de Redes"));
        Resp ok = llamar("POST", "/api/solicitudes", est, solicitud(1, "2026-11-20", "08:00", "10:00", "Práctica"));
        verificar("HU-02 Esc.1: solicitud registrada → «Pendiente»", ok.codigo() == 200 && ok.contiene("\"estado\":\"Pendiente\""));
        Resp falta = llamar("POST", "/api/solicitudes", est, solicitud(1, "2026-11-20", "08:00", "10:00", ""));
        verificar("HU-02 Esc.2: motivo vacío → 400 e indica el campo", falta.codigo() == 400 && falta.contiene("motivo"));
        verificar("HU-02 Esc.3: fecha pasada → 400 horario no válido", llamar("POST", "/api/solicitudes", est, solicitud(1, "2026-11-01", "08:00", "10:00", "x")).contiene("no es válido"));
        verificar("HU-02 Esc.3: fin ≤ inicio → 400 horario no válido", llamar("POST", "/api/solicitudes", est, solicitud(1, "2026-11-20", "10:00", "08:00", "x")).codigo() == 400);
        verificar("HU-03: la solicitud del docente por lo mismo también queda «Pendiente»", llamar("POST", "/api/solicitudes", doc, solicitud(1, "2026-11-20", "08:00", "10:00", "Otra práctica")).contiene("Pendiente"));
        verificar("HU-02 (RN-02): el encargado no registra solicitudes (403)", llamar("POST", "/api/solicitudes", enc, solicitud(3, "2026-11-25", "08:00", "10:00", "x")).codigo() == 403);

        titulo("API · HU-07 / HU-04 / RN-02");
        verificar("HU-07 (RN-02): el estudiante no puede ver pendientes (403)", llamar("GET", "/api/solicitudes/pendientes", est, null).codigo() == 403);
        verificar("HU-07 (RN-02): el estudiante no puede aprobar (403)", llamar("POST", "/api/solicitudes/1/aprobar", est, null).codigo() == 403);
        verificar("HU-07: el encargado ve las 2 pendientes", llamar("GET", "/api/solicitudes/pendientes", enc, null).cuerpo().split("\"id\"").length == 3);
        verificar("HU-07 Esc.1: el encargado aprueba la primera → «Aprobada»", llamar("POST", "/api/solicitudes/1/aprobar", enc, null).contiene("Aprobada"));
        Resp cruce = llamar("POST", "/api/solicitudes/2/aprobar", enc, null);
        verificar("HU-07 Esc.3 / HU-03 Esc.2: aprobar la cruzada → 409 recurso ya reservado", cruce.codigo() == 409 && cruce.contiene("ya está reservado"));
        verificar("HU-07 (State): aprobar de nuevo la ya aprobada → 409", llamar("POST", "/api/solicitudes/1/aprobar", enc, null).codigo() == 409);
        verificar("HU-07 Esc.2: el encargado rechaza la otra → «Rechazada»", llamar("POST", "/api/solicitudes/2/rechazar", enc, null).contiene("Rechazada"));
        verificar("HU-04 Esc.1: el estudiante ve su notificación de aprobada", llamar("GET", "/api/notificaciones", est, null).contiene("APROBADA"));
        verificar("HU-04 Esc.2: el docente ve su notificación de rechazada", llamar("GET", "/api/notificaciones", doc, null).contiene("RECHAZADA"));
        verificar("HU-03 Esc.3 (RN-05): el estudiante no puede pedir otro recurso a la misma hora", llamar("POST", "/api/solicitudes", est, solicitud(2, "2026-11-20", "09:00", "10:30", "x")).codigo() == 409);

        titulo("API · HU-05 / HU-08");
        verificar("HU-05: el estudiante ve su préstamo «Activo»", llamar("GET", "/api/prestamos/mios", est, null).contiene("\"estado\":\"Activo\""));
        verificar("HU-05 Esc.2: el docente (sin préstamos) recibe lista vacía", llamar("GET", "/api/prestamos/mios", doc, null).cuerpo().equals("[]"));
        verificar("HU-08 Esc.3: el estudiante no puede registrar la devolución (403, rol sin permiso)", llamar("POST", "/api/prestamos/1/devolver", est, null).codigo() == 403);
        verificar("HU-08 (RN-02): el estudiante no ve los préstamos activos de todos (403)", llamar("GET", "/api/prestamos/activos", est, null).codigo() == 403);
        verificar("HU-08: el encargado ve el préstamo activo", llamar("GET", "/api/prestamos/activos", enc, null).contiene("Laboratorio de Redes"));
        verificar("HU-08 Esc.1: el encargado registra la devolución → «Finalizado»", llamar("POST", "/api/prestamos/1/devolver", enc, null).contiene("Finalizado"));
        Resp otra = llamar("POST", "/api/prestamos/1/devolver", enc, null);
        verificar("HU-08 Esc.2: devolver otra vez → 409 «ya fue devuelto»", otra.codigo() == 409 && otra.contiene("ya fue devuelto"));
        verificar("HU-05 Esc.3: el estudiante ve el préstamo «Finalizado»", llamar("GET", "/api/prestamos/mios", est, null).contiene("\"estado\":\"Finalizado\""));

        titulo("API · HU-06 / HU-10");
        verificar("HU-06 (RN-02): el estudiante no puede crear recursos (403)", llamar("POST", "/api/recursos", est, "{\"tipo\":\"LABORATORIO\",\"nombre\":\"X\",\"ubicacion\":\"Y\",\"capacidad\":\"5\"}").codigo() == 403);
        Resp nuevo = llamar("POST", "/api/recursos", enc, "{\"tipo\":\"EQUIPO\",\"nombre\":\"Multímetro\",\"marca\":\"Fluke\",\"modelo\":\"117\",\"numeroSerie\":\"MUL-01\"}");
        verificar("HU-06 Esc.1: el encargado registra un equipo «Disponible»", nuevo.codigo() == 200 && nuevo.contiene("Disponible"));
        verificar("HU-06 Esc.2: modifica el equipo", llamar("PUT", "/api/recursos/5", enc, "{\"nombre\":\"Multímetro digital\"}").contiene("Multímetro digital"));
        verificar("HU-06 Esc.3: elimina el equipo sin trámites", llamar("DELETE", "/api/recursos/5", enc, null).codigo() == 200);
        llamar("POST", "/api/solicitudes", est, solicitud(2, "2026-11-25", "08:00", "10:00", "Práctica"));
        Resp vig = llamar("DELETE", "/api/recursos/2", enc, null);
        verificar("HU-06 Esc.4: recurso con solicitud pendiente → 409 e informa vigentes", vig.codigo() == 409 && vig.contiene("vigentes"));
        verificar("HU-10 (RN-02): el estudiante no puede listar ni crear usuarios (403)", llamar("GET", "/api/usuarios", est, null).codigo() == 403 && llamar("POST", "/api/usuarios", est, "{}").codigo() == 403);
        String alta = "{\"nombre\":\"Laura Pérez\",\"correo\":\"laura@itm.edu.co\",\"contrasena\":\"Clave123\",\"rol\":\"DOCENTE\"}";
        verificar("HU-10 Esc.1: el encargado crea una cuenta con rol", llamar("POST", "/api/usuarios", enc, alta).contiene("DOCENTE"));
        verificar("HU-10 Esc.1: el nuevo usuario inicia sesión", entrar("laura@itm.edu.co", "Clave123") != null);
        verificar("HU-10 Esc.2: correo duplicado → 409", llamar("POST", "/api/usuarios", enc, alta).codigo() == 409);
        verificar("HU-10 Esc.3: sin rol → 400 e indica qué falta", llamar("POST", "/api/usuarios", enc, "{\"nombre\":\"A\",\"correo\":\"a@itm.edu.co\",\"contrasena\":\"x\"}").contiene("rol"));
        verificar("El listado de usuarios nunca incluye contraseñas", !llamar("GET", "/api/usuarios", enc, null).contiene("contrasena"));
        llamar("DELETE", "/api/sesion", est, null);
        verificar("Cerrar sesión invalida el token (401)", llamar("GET", "/api/recursos", est, null).codigo() == 401);
    }
}
