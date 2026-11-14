package co.itm.prestamo;

import static co.itm.prestamo.Verificador.*;

import co.itm.prestamo.dominio.*;
import co.itm.prestamo.excepciones.*;
import co.itm.prestamo.prestamos.PrestamoOperaciones;
import co.itm.prestamo.solicitudes.NuevaSolicitud;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Pruebas de las 10 historias de usuario: un grupo por HU y una comprobación por escenario Gherkin
 * (más las reglas RN-01 a RN-07 y RNF-03, RNF-05 y RNF-06). Sin librerías: se ejecutan con pruebas.sh.
 */
public class PruebasFlujo {
    /** Reloj controlable: las pruebas fijan «ahora» (por defecto 10-nov-2026 08:00) y lo adelantan cuando lo necesitan. */
    static class RelojPrueba extends Clock {
        private Instant ahora = Instant.parse("2026-11-10T13:00:00Z"); static final ZoneId ZONA = ZoneId.of("America/Bogota");
        void fijar(LocalDateTime t) { ahora = t.atZone(ZONA).toInstant(); }
        @Override public ZoneId getZone() { return ZONA; } @Override public Clock withZone(ZoneId z) { return this; }
        @Override public Instant instant() { return ahora; }
    }
    static final RelojPrueba RELOJ = new RelojPrueba();
    static final LocalDate DIA = LocalDate.of(2026, 11, 20), DIA2 = LocalDate.of(2026, 11, 21);
    static final LocalTime H8 = LocalTime.of(8, 0), H9 = LocalTime.of(9, 0), H10 = LocalTime.of(10, 0), H11 = LocalTime.of(11, 0), H12 = LocalTime.of(12, 0);

    static Main.Sistema s;
    static Usuario est, doc, enc;
    static void nuevo() {
        RELOJ.fijar(LocalDateTime.of(2026, 11, 10, 8, 0));
        // PRUEBAS_BD=postgres: las mismas pruebas contra un PostgreSQL real (variables PG*). Sin ella, tablas en memoria.
        co.itm.prestamo.persistencia.Tablas tablas = System.getenv("PRUEBAS_BD") != null
            ? co.itm.prestamo.contrato.TablasPsql.limpias() : co.itm.prestamo.persistencia.TablasMemoria.crear();
        s = Main.armar(RELOJ, tablas);
        Main.cargarDatosDeDemostracion(s.usuarios(), s.recursos());
        est = s.usuarios().buscar(1).get(); doc = s.usuarios().buscar(2).get(); enc = s.usuarios().buscar(3).get();
    }
    static NuevaSolicitud pedir(long recurso, LocalDate d, LocalTime i, LocalTime f) { return new NuevaSolicitud(recurso, d, i, f, "Práctica de laboratorio"); }
    static Solicitud registrar(Usuario u, long recurso, LocalDate d, LocalTime i, LocalTime f) { return s.solicitudes().registrarSolicitud(u, pedir(recurso, d, i, f)); }
    static Solicitud aprobada(Usuario u, long recurso, LocalDate d, LocalTime i, LocalTime f) { Solicitud x = registrar(u, recurso, d, i, f); return s.solicitudes().aprobarSolicitud(enc, x.getId()); }
    static long prestamoDe(Usuario u) { return s.prestamos().listarPrestamosUsuario(u).get(0).getId(); }
    static List<String> nombres(List<Recurso> l) { List<String> r = new ArrayList<>(); for (Recurso x : l) r.add(x.getNombre()); return r; }

    public static void main(String[] args) throws Exception {
        hu09(); hu10(); hu06(); hu01(); hu02(); hu03(); hu07(); hu04(); hu08(); hu05(); reglas(); rnf();
        resumen("PruebasFlujo");
        if (fallidas() > 0) System.exit(1);
    }

    static void hu09() {
        titulo("HU-09 Iniciar sesión de forma segura"); nuevo();
        Usuario u = s.usuarios().autenticar("estudiante@itm.edu.co", "Estudiante123");
        verificar("HU-09 Esc.1: credenciales correctas → accede y se identifica su rol", u.getRol() == RolUsuario.ESTUDIANTE);
        verificar("HU-09 Esc.1: el estudiante solo tiene la función de solicitar (no decide ni administra)",
            s.usuarios().tienePermiso(u, co.itm.prestamo.puertos.Operacion.REGISTRAR_SOLICITUD) && !s.usuarios().tienePermiso(u, co.itm.prestamo.puertos.Operacion.DECIDIR_SOLICITUD));
        verificar("HU-09 Esc.2: contraseña incorrecta → acceso denegado con mensaje", lanza(CredencialesInvalidasException.class, () -> s.usuarios().autenticar("estudiante@itm.edu.co", "mala")));
        verificar("HU-09 Esc.2: usuario inexistente → acceso denegado con mensaje", lanza(CredencialesInvalidasException.class, () -> s.usuarios().autenticar("nadie@itm.edu.co", "Estudiante123")));
        verificar("HU-09 Esc.3: campos vacíos → indica cuáles son obligatorios", mensaje(() -> s.usuarios().autenticar("", "")).contains("correo, contraseña"));
        verificar("HU-09 Esc.3: solo falta la contraseña → se indica", mensaje(() -> s.usuarios().autenticar("estudiante@itm.edu.co", " ")).contains("contraseña"));
        verificar("RNF-06: la contraseña no se guarda en texto claro", !u.getContrasena().contains("Estudiante123") && u.getContrasena().split(":").length == 3);
        String t = s.sesiones().abrir(u);
        verificar("HU-09: la sesión abierta devuelve al mismo usuario y se puede cerrar", s.sesiones().usuarioDe(t).get() == u && cerrar(t));
    }
    static boolean cerrar(String t) { s.sesiones().cerrar(t); return s.sesiones().usuarioDe(t).isEmpty(); }

    static void hu10() {
        titulo("HU-10 Registrar y administrar usuarios"); nuevo();
        Usuario nuevoU = s.usuarios().registrarUsuario(enc, "Laura Pérez", "laura@itm.edu.co", "Clave123", "DOCENTE");
        verificar("HU-10 Esc.1: la cuenta queda creada con el rol asignado", nuevoU.getRol() == RolUsuario.DOCENTE && s.usuarios().existeCorreo("laura@itm.edu.co"));
        verificar("HU-10 Esc.1: el nuevo usuario puede iniciar sesión", s.usuarios().autenticar("laura@itm.edu.co", "Clave123").getId() == nuevoU.getId());
        verificar("HU-10 Esc.2 (RN-07): correo duplicado → rechaza con mensaje", lanza(CorreoDuplicadoException.class, () -> s.usuarios().registrarUsuario(enc, "Otra", "LAURA@itm.edu.co", "x", "ESTUDIANTE")));
        verificar("HU-10 Esc.3: sin rol → no crea la cuenta e indica el dato faltante", mensaje(() -> s.usuarios().registrarUsuario(enc, "Sin Rol", "sinrol@itm.edu.co", "x", "")).contains("rol"));
        verificar("HU-10 Esc.3: campos vacíos → no crea la cuenta", lanza(CamposIncompletosException.class, () -> s.usuarios().registrarUsuario(enc, "", "", "", "ESTUDIANTE")) && !s.usuarios().existeCorreo("sinrol@itm.edu.co"));
        verificar("HU-10: solo el encargado administra usuarios (RN-02)", lanza(AccesoDenegadoException.class, () -> s.usuarios().registrarUsuario(est, "X", "x@itm.edu.co", "x", "ESTUDIANTE")));
    }

    static void hu06() {
        titulo("HU-06 Administrar laboratorios y equipos"); nuevo();
        Recurso lab = s.recursos().registrarRecurso(enc, "LABORATORIO", Map.of("nombre", "Laboratorio de Física", "ubicacion", "Bloque F", "capacidad", "25"));
        Recurso eq = s.recursos().registrarRecurso(enc, "EQUIPO", Map.of("nombre", "Osciloscopio", "marca", "Rigol", "modelo", "DS1054Z", "numeroSerie", "OSC-01"));
        verificar("HU-06 Esc.1: el laboratorio queda en el inventario «Disponible» (Factory Method)", lab instanceof Laboratorio && lab.getEstado() == EstadoRecurso.DISPONIBLE && s.recursos().consultarRecursos().stream().anyMatch(r -> r.getId() == lab.getId()));
        verificar("HU-06 Esc.1: el equipo queda en el inventario «Disponible» (Factory Method)", eq instanceof Equipo && eq.getEstado() == EstadoRecurso.DISPONIBLE);
        verificar("HU-06 Esc.1 (alterno): faltan campos → se indican y no se registra", mensaje(() -> s.recursos().registrarRecurso(enc, "EQUIPO", Map.of("nombre", "X"))).contains("marca, modelo, numeroSerie"));
        s.recursos().modificarRecurso(enc, eq.getId(), Map.of("nombre", "Osciloscopio digital", "marca", "Siglent"));
        verificar("HU-06 Esc.2: modificar muestra la información actualizada", s.recursos().consultarRecursos().stream().anyMatch(r -> r.getNombre().equals("Osciloscopio digital") && r.getDetalle().contains("Siglent")));
        s.recursos().eliminarRecurso(enc, eq.getId());
        verificar("HU-06 Esc.3: sin trámites vigentes → se elimina y deja de aparecer", s.recursos().consultarRecursos().stream().noneMatch(r -> r.getId() == eq.getId())
            && !nombres(s.recursos().consultarDisponibles(DIA, H8, H10)).contains("Osciloscopio digital"));
        Solicitud pend = registrar(est, 1, DIA, H8, H10);
        verificar("HU-06 Esc.4: solicitud pendiente → no se elimina e informa vigentes (RN-06)", mensaje(() -> s.recursos().eliminarRecurso(enc, 1)).contains("vigentes"));
        s.solicitudes().aprobarSolicitud(enc, pend.getId());
        verificar("HU-06 Esc.4: solicitud aprobada / préstamo activo → no se elimina (RN-06)", lanza(RecursoConSolicitudesVigentesException.class, () -> s.recursos().eliminarRecurso(enc, 1)));
        Solicitud vieja = registrar(doc, 2, DIA, H8, H10); s.solicitudes().rechazarSolicitud(enc, vieja.getId());
        s.recursos().eliminarRecurso(enc, 2);
        verificar("HU-06 (RN-06): al eliminar un recurso se conserva el historial de sus solicitudes (baja lógica)", s.solicitudes().listarDeUsuario(doc).stream().anyMatch(x -> x.getId() == vieja.getId())
            && s.recursos().consultarRecursos().stream().noneMatch(r -> r.getId() == 2));
        verificar("HU-06: el estudiante no administra inventario (RN-02)", lanza(AccesoDenegadoException.class, () -> s.recursos().eliminarRecurso(est, lab.getId())));
        verificar("HU-06: tipo de recurso inválido → mensaje", lanza(CamposIncompletosException.class, () -> s.recursos().registrarRecurso(enc, "SALON", Map.of("nombre", "X"))));
    }

    static void hu01() {
        titulo("HU-01 Consultar disponibilidad de recursos"); nuevo();
        aprobada(doc, 1, DIA, H8, H10);
        List<String> libres = nombres(s.recursos().consultarDisponibles(DIA, H9, H11));
        verificar("HU-01 Esc.1: muestra solo los recursos sin reserva aprobada en el rango", libres.size() == 3 && !libres.contains("Laboratorio de Redes"));
        verificar("HU-01 Esc.1: en otro horario el recurso reservado vuelve a aparecer", nombres(s.recursos().consultarDisponibles(DIA, H10, H12)).contains("Laboratorio de Redes"));
        verificar("HU-01 Esc.1: una solicitud solo pendiente no oculta el recurso (RN-01)", registrar(est, 2, DIA, H9, H11) != null && nombres(s.recursos().consultarDisponibles(DIA, H9, H11)).contains("Laboratorio de Robótica"));
        nuevo(); Usuario otro = s.usuarios().registrarUsuario(enc, "Otra Persona", "otra@itm.edu.co", "Clave123", "ESTUDIANTE");
        aprobada(doc, 1, DIA, H8, H10); aprobada(est, 2, DIA, H8, H10);
        Usuario tercero = s.usuarios().registrarUsuario(enc, "Tercera Persona", "tercera@itm.edu.co", "Clave123", "DOCENTE");
        aprobada(tercero, 3, DIA, H8, H10); aprobada(otro, 4, DIA, H8, H10);
        verificar("HU-01 Esc.2: todos reservados → lista vacía (la interfaz muestra «no hay recursos disponibles»)", s.recursos().consultarDisponibles(DIA, H8, H10).isEmpty());
        nuevo(); aprobada(est, 3, DIA, H8, H10); long pid = prestamoDe(est);
        verificar("HU-01 (RN-04): antes de que termine el horario del préstamo, una fecha posterior sigue libre", nombres(s.recursos().consultarDisponibles(DIA2, H8, H10)).contains("Impresora 3D"));
        RELOJ.fijar(LocalDateTime.of(2026, 11, 20, 11, 0));   // el horario terminó (10:00) y nadie registró la devolución
        verificar("HU-01 Esc.3 (RN-04): equipo prestado sin devolución → no aparece en fecha y horario posteriores", !nombres(s.recursos().consultarDisponibles(DIA2, H8, H10)).contains("Impresora 3D") && !nombres(s.recursos().consultarDisponibles(DIA, H11, H12)).contains("Impresora 3D"));
        verificar("HU-01 Esc.3: los demás recursos siguen apareciendo", nombres(s.recursos().consultarDisponibles(DIA2, H8, H10)).contains("Gafas de realidad virtual"));
        s.prestamos().registrarDevolucion(enc, pid);
        verificar("HU-01: registrada la devolución, el equipo vuelve a aparecer", nombres(s.recursos().consultarDisponibles(DIA2, H8, H10)).contains("Impresora 3D") && nombres(s.recursos().consultarDisponibles(DIA, H8, H10)).contains("Impresora 3D"));
        verificar("HU-01: sin fecha u horario → se indican los campos faltantes", lanza(CamposIncompletosException.class, () -> s.recursos().consultarDisponibles(null, H8, H10)));
        verificar("HU-01: rango inválido (fin ≤ inicio) → horario no válido", lanza(HorarioInvalidoException.class, () -> s.recursos().consultarDisponibles(DIA, H10, H10)));
    }

    /** Lee el estado guardado del recurso (como lo vería otra petición), no el objeto que ya teníamos en la mano. */
    static EstadoRecurso estadoDe(long id) {
        for (Recurso r : s.recursos().consultarRecursos()) if (r.getId() == id) return r.getEstado();
        throw new IllegalStateException("recurso " + id);
    }

    static void hu02() {
        titulo("HU-02 Solicitar préstamo de un recurso"); nuevo();
        Solicitud a = registrar(est, 1, DIA, H8, H10);
        verificar("HU-02 Esc.1: la solicitud se guarda «Pendiente» (RN-03)", a.getEstado() == EstadoSolicitud.PENDIENTE && s.solicitudes().listarDeUsuario(est).stream().anyMatch(x -> x.getId() == a.getId()));
        verificar("HU-02 Esc.1: queda registrada la fecha de registro", a.getFechaRegistro() != null);
        String m = mensaje(() -> s.solicitudes().registrarSolicitud(est, new NuevaSolicitud(null, null, H8, null, " ")));
        verificar("HU-02 Esc.2: campos vacíos → no se registra e indica cuáles faltan", m.contains("recurso") && m.contains("fecha") && m.contains("hora de fin") && m.contains("motivo") && s.solicitudes().listarDeUsuario(est).size() == 1);
        verificar("HU-02 Esc.3: fecha pasada → horario no válido y no se registra", lanza(HorarioInvalidoException.class, () -> registrar(est, 1, LocalDate.of(2026, 11, 9), H8, H10)));
        verificar("HU-02 Esc.3: fin anterior al inicio → horario no válido", lanza(HorarioInvalidoException.class, () -> registrar(est, 1, DIA, H10, H8)));
        verificar("HU-02 Esc.3: fin igual al inicio → horario no válido", lanza(HorarioInvalidoException.class, () -> registrar(est, 1, DIA, H10, H10)));
        verificar("HU-02 Esc.3: el mensaje dice que el horario no es válido", mensaje(() -> registrar(est, 1, DIA, H10, H8)).contains("no es válido"));
        verificar("HU-02: hoy mismo sí es una fecha válida", registrar(est, 2, LocalDate.of(2026, 11, 10), H8, H10) != null);
        verificar("HU-02 Esc.3: hoy con la hora de inicio ya pasada → horario no válido", mensaje(() -> registrar(doc, 2, LocalDate.of(2026, 11, 10), LocalTime.of(7, 0), H9)).contains("ya pasó"));
        verificar("HU-02 (RN-02): el encargado no registra solicitudes (solo estudiante o docente)", lanza(AccesoDenegadoException.class, () -> registrar(enc, 1, DIA2, H8, H10)));
        verificar("HU-02: recurso inexistente → error", lanza(RecursoNoEncontradoException.class, () -> registrar(est, 99, DIA, H8, H10)));
    }

    static void hu03() throws Exception {
        titulo("HU-03 Bloquear solicitudes en conflicto de horario"); nuevo();
        aprobada(est, 1, DIA, H8, H10);
        verificar("HU-03 Esc.1: recurso con reserva aprobada que se cruza → bloquea el registro (RN-01)", lanza(ConflictoHorarioException.class, () -> registrar(doc, 1, DIA, H9, H11)));
        verificar("HU-03 Esc.1: el aviso dice que el recurso no está disponible en ese horario", mensaje(() -> registrar(doc, 1, DIA, H9, H11)).contains("no está disponible"));
        verificar("HU-03 Esc.1: horario contiguo (10:00–12:00) no se cruza → permitido", registrar(doc, 1, DIA, H10, H12) != null);
        verificar("HU-03 Esc.1: mismo horario en otro día → permitido", registrar(doc, 1, DIA2, H8, H10) != null);
        nuevo();
        Solicitud x = registrar(est, 1, DIA, H8, H10), y = registrar(doc, 1, DIA, H8, H10);
        verificar("HU-03 Esc.2: dos solicitudes por el mismo recurso y horario coexisten «Pendientes» (RN-01 solo mira aprobadas)", x.estaPendiente() && y.estaPendiente());
        s.solicitudes().aprobarSolicitud(enc, x.getId());
        verificar("HU-03 Esc.2: solo una queda aprobada; la otra no se puede aprobar por conflicto", lanza(ConflictoHorarioException.class, () -> s.solicitudes().aprobarSolicitud(enc, y.getId())) && y.estaPendiente());
        // RNF-05: simultaneidad real con hilos, repetida varias veces
        int dobles = 0;
        for (int i = 0; i < 30; i++) {
            nuevo();
            Solicitud p = registrar(est, 1, DIA, H8, H10), q = registrar(doc, 1, DIA, H8, H10);
            CountDownLatch salida = new CountDownLatch(1); ExecutorService ex = Executors.newFixedThreadPool(2);
            Callable<Boolean> a1 = () -> { salida.await(); try { s.solicitudes().aprobarSolicitud(enc, p.getId()); return true; } catch (ConflictoHorarioException e) { return false; } };
            Callable<Boolean> a2 = () -> { salida.await(); try { s.solicitudes().aprobarSolicitud(enc, q.getId()); return true; } catch (ConflictoHorarioException e) { return false; } };
            Future<Boolean> f1 = ex.submit(a1), f2 = ex.submit(a2); salida.countDown();
            boolean r1 = f1.get(), r2 = f2.get(); ex.shutdown();
            if (r1 && r2) dobles++;
            if (!(r1 ^ r2)) dobles += 100;
        }
        verificar("HU-03 Esc.2 / RNF-05: 30 rondas de aprobaciones simultáneas → nunca quedan dos aprobadas", dobles == 0);
        nuevo();
        Solicitud r = registrar(est, 1, DIA, H8, H10); CountDownLatch go = new CountDownLatch(1); ExecutorService ex = Executors.newFixedThreadPool(2);
        Future<Solicitud> g1 = ex.submit(() -> { go.await(); return registrar(est, 2, DIA2, H8, H10); });
        Future<Solicitud> g2 = ex.submit(() -> { go.await(); return registrar(doc, 2, DIA2, H8, H10); }); go.countDown();
        verificar("HU-03 Esc.2: dos registros simultáneos terminan ambos «Pendientes» (ninguno se pierde)", g1.get().estaPendiente() && g2.get().estaPendiente() && s.solicitudes().listarPendientes(enc).size() == 3); ex.shutdown();
        nuevo(); aprobada(est, 1, DIA, H8, H10);
        verificar("HU-03 Esc.3: mismo usuario con reserva aprobada → bloquea otra que se cruza (RN-05)", lanza(ConflictoHorarioException.class, () -> registrar(est, 2, DIA, H9, H11)));
        verificar("HU-03 Esc.3: el aviso informa que ya tiene una reserva en ese horario", mensaje(() -> registrar(est, 2, DIA, H9, H11)).contains("ya tiene una reserva"));
        verificar("HU-03 Esc.3: el mismo usuario en un horario que no se cruza → permitido", registrar(est, 2, DIA, H10, H12) != null);
        verificar("HU-03: otro usuario en otro recurso, mismo horario → permitido", registrar(doc, 2, DIA, H8, H10) != null);
    }

    static void hu07() {
        titulo("HU-07 Aprobar o rechazar solicitudes"); nuevo();
        Solicitud a = registrar(est, 1, DIA, H8, H10), b = registrar(doc, 2, DIA, H8, H10), c = registrar(est, 3, DIA2, H8, H10);
        verificar("HU-07: las pendientes se listan en orden de llegada", s.solicitudes().listarPendientes(enc).stream().map(Solicitud::getId).toList().equals(List.of(a.getId(), b.getId(), c.getId())));
        verificar("HU-07 (RN-02): el estudiante no puede aprobar (Proxy)", lanza(AccesoDenegadoException.class, () -> s.solicitudes().aprobarSolicitud(est, a.getId())));
        verificar("HU-07 (RN-02): el docente no puede rechazar (Proxy)", lanza(AccesoDenegadoException.class, () -> s.solicitudes().rechazarSolicitud(doc, a.getId())));
        Solicitud ap = s.solicitudes().aprobarSolicitud(enc, a.getId());
        verificar("HU-07 Esc.1: aprobar → «Aprobada»", ap.getEstado() == EstadoSolicitud.APROBADA);
        verificar("HU-07 Esc.1: el solicitante recibe la notificación (Observer)", s.notificaciones().deUsuario(est.getId()).size() == 1 && s.notificaciones().deUsuario(est.getId()).get(0).getMensaje().contains("APROBADA"));
        verificar("HU-07 Esc.1: se crea el préstamo «Activo» y el recurso pasa a «Prestado» (Observer)", s.prestamos().listarPrestamosActivos(enc).size() == 1 && ap.getRecurso().getEstado() == EstadoRecurso.PRESTADO);
        Solicitud re = s.solicitudes().rechazarSolicitud(enc, b.getId());
        verificar("HU-07 Esc.2: rechazar → «Rechazada»", re.getEstado() == EstadoSolicitud.RECHAZADA);
        verificar("HU-07 Esc.2: el solicitante recibe la notificación y no se crea préstamo", s.notificaciones().deUsuario(doc.getId()).get(0).getMensaje().contains("RECHAZADA") && s.prestamos().listarPrestamosActivos(enc).size() == 1);
        verificar("HU-07 (State): una solicitud ya decidida no se puede decidir otra vez", lanza(TransicionInvalidaException.class, () -> s.solicitudes().rechazarSolicitud(enc, a.getId())) && lanza(TransicionInvalidaException.class, () -> s.solicitudes().aprobarSolicitud(enc, b.getId())));
        verificar("HU-07: solicitud inexistente → error", lanza(RecursoNoEncontradoException.class, () -> s.solicitudes().aprobarSolicitud(enc, 99)));
        nuevo();
        Solicitud p = registrar(est, 1, DIA, H8, H10), q = registrar(doc, 1, DIA, H9, H11);
        s.solicitudes().aprobarSolicitud(enc, p.getId());
        verificar("HU-07 Esc.3: aprobar otra que se cruza con una ya aprobada → no se permite", lanza(ConflictoHorarioException.class, () -> s.solicitudes().aprobarSolicitud(enc, q.getId())));
        verificar("HU-07 Esc.3: informa que el recurso ya está reservado y la solicitud sigue «Pendiente»", mensaje(() -> s.solicitudes().aprobarSolicitud(enc, q.getId())).contains("ya está reservado") && q.estaPendiente());
        verificar("HU-07 Esc.3: sí puede rechazarla", s.solicitudes().rechazarSolicitud(enc, q.getId()).getEstado() == EstadoSolicitud.RECHAZADA);
        nuevo();
        Solicitud u1 = registrar(est, 1, DIA, H8, H10), u2 = registrar(est, 2, DIA, H9, H11);
        s.solicitudes().aprobarSolicitud(enc, u1.getId());
        verificar("HU-07 / RN-05: no se aprueba una segunda reserva cruzada del mismo usuario", lanza(ConflictoHorarioException.class, () -> s.solicitudes().aprobarSolicitud(enc, u2.getId())));
        nuevo();
        Solicitud vencida = registrar(est, 1, DIA, H8, H10);
        RELOJ.fijar(LocalDateTime.of(2026, 11, 20, 9, 0));   // el encargado la revisa cuando su horario ya empezó
        verificar("HU-07: una pendiente cuyo horario ya pasó no se aprueba (no nace un préstamo vencido, RN-04)", lanza(HorarioInvalidoException.class, () -> s.solicitudes().aprobarSolicitud(enc, vencida.getId())) && vencida.estaPendiente() && s.prestamos().listarPrestamosActivos(enc).isEmpty());
        verificar("HU-07: esa solicitud vencida sí se puede rechazar", s.solicitudes().rechazarSolicitud(enc, vencida.getId()).getEstado() == EstadoSolicitud.RECHAZADA);
        nuevo();
        Solicitud ok = registrar(est, 1, DIA, H8, H10), rech = registrar(doc, 1, DIA, H9, H11);
        s.solicitudes().rechazarSolicitud(enc, rech.getId()); s.solicitudes().aprobarSolicitud(enc, ok.getId());
        verificar("HU-07 (State): aprobar una ya rechazada informa la transición inválida, aunque se cruce con otra", lanza(TransicionInvalidaException.class, () -> s.solicitudes().aprobarSolicitud(enc, rech.getId())));
    }

    static void hu04() {
        titulo("HU-04 Notificar estado de la solicitud"); nuevo();
        Solicitud a = registrar(est, 1, DIA, H8, H10), b = registrar(est, 2, DIA, H8, H10), c = registrar(est, 3, DIA2, H8, H10);
        s.solicitudes().aprobarSolicitud(enc, a.getId()); s.solicitudes().rechazarSolicitud(enc, b.getId());
        List<Notificacion> ns = s.notificaciones().deUsuario(est.getId());
        verificar("HU-04 Esc.1: al iniciar sesión veo la notificación de solicitud aprobada", ns.stream().anyMatch(n -> n.getMensaje().contains("#" + a.getId()) && n.getMensaje().contains("APROBADA")));
        verificar("HU-04 Esc.2: veo la notificación de solicitud rechazada", ns.stream().anyMatch(n -> n.getMensaje().contains("#" + b.getId()) && n.getMensaje().contains("RECHAZADA")));
        verificar("HU-04 Esc.3: la solicitud sin respuesta no genera notificación y sigue «Pendiente»", ns.stream().noneMatch(n -> n.getMensaje().contains("#" + c.getId())) && c.estaPendiente());
        verificar("HU-04: cada usuario ve solo sus avisos", s.notificaciones().deUsuario(doc.getId()).isEmpty());
        verificar("HU-04: los avisos nacen sin leer y se pueden marcar como leídos", ns.stream().noneMatch(Notificacion::isLeida) && marcar());
    }
    static boolean marcar() { s.notificaciones().marcarLeidas(est.getId()); return s.notificaciones().deUsuario(est.getId()).stream().allMatch(Notificacion::isLeida); }

    static void hu08() {
        titulo("HU-08 Registrar la devolución de un recurso"); nuevo();
        Solicitud a = aprobada(est, 3, DIA, H8, H10); long pid = prestamoDe(est);
        Prestamo pr = s.prestamos().registrarDevolucion(enc, pid);
        verificar("HU-08 Esc.1: la devolución deja el préstamo «Finalizado» con fecha de devolución", pr.getEstado() == EstadoPrestamo.FINALIZADO && pr.getFechaDevolucion() != null);
        verificar("HU-08 Esc.1: el recurso vuelve a estar «Disponible»", estadoDe(a.getRecurso().getId()) == EstadoRecurso.DISPONIBLE);
        verificar("HU-08 Esc.2: devolver un préstamo ya finalizado → no se permite e informa que ya fue devuelto", mensaje(() -> s.prestamos().registrarDevolucion(enc, pid)).contains("ya fue devuelto"));
        nuevo(); aprobada(est, 3, DIA, H8, H10); long id2 = prestamoDe(est);
        verificar("HU-08 Esc.3: el estudiante no puede registrar devoluciones (Proxy)", lanza(AccesoDenegadoException.class, () -> s.prestamos().registrarDevolucion(est, id2)));
        verificar("HU-08 Esc.3: el docente tampoco, con mensaje de rol sin permiso", mensaje(() -> s.prestamos().registrarDevolucion(doc, id2)).contains("rol no tiene permiso"));
        verificar("HU-08 Esc.3: el préstamo sigue «Activo» tras los intentos denegados", s.prestamos().listarPrestamosActivos(enc).size() == 1);
        verificar("HU-08: préstamo inexistente → error", lanza(RecursoNoEncontradoException.class, () -> s.prestamos().registrarDevolucion(enc, 99)));
        verificar("HU-08 (RN-02): solo el encargado ve los préstamos activos", lanza(AccesoDenegadoException.class, () -> s.prestamos().listarPrestamosActivos(est)));
        // RN-04
        nuevo(); aprobada(est, 3, DIA, H8, H10); Solicitud y = registrar(doc, 3, DIA2, H8, H10); long pid3 = prestamoDe(est);
        RELOJ.fijar(LocalDateTime.of(2026, 11, 20, 11, 0));   // terminó el horario y no se registró la devolución
        verificar("RN-04: con el préstamo vencido sin devolver no se puede aprobar otra reserva posterior del mismo recurso", lanza(ConflictoHorarioException.class, () -> s.solicitudes().aprobarSolicitud(enc, y.getId())));
        verificar("RN-04: ni registrar una nueva posterior mientras no se devuelva", lanza(ConflictoHorarioException.class, () -> registrar(doc, 3, DIA2, H10, H12)));
        s.prestamos().registrarDevolucion(enc, pid3);
        verificar("RN-04: devuelto el recurso, la reserva posterior ya se puede aprobar", s.solicitudes().aprobarSolicitud(enc, y.getId()).estaAprobada());
    }

    static void hu05() {
        titulo("HU-05 Consultar el estado de mis préstamos"); nuevo();
        verificar("HU-05 Esc.2: usuario sin préstamos → lista vacía (la interfaz muestra el mensaje)", s.prestamos().listarPrestamosUsuario(est).isEmpty());
        aprobada(est, 1, DIA, H8, H10); aprobada(est, 3, DIA2, H8, H10);
        s.prestamos().registrarDevolucion(enc, s.prestamos().listarPrestamosUsuario(est).get(0).getId());
        List<Prestamo> mios = s.prestamos().listarPrestamosUsuario(est);
        verificar("HU-05 Esc.1: veo préstamos activos y finalizados, cada uno con su estado", mios.size() == 2 && mios.get(0).getEstado() == EstadoPrestamo.FINALIZADO && mios.get(1).getEstado() == EstadoPrestamo.ACTIVO);
        verificar("HU-05 Esc.3: el préstamo devuelto aparece «Finalizado»", mios.get(0).getEstado().getEtiqueta().equals("Finalizado"));
        verificar("HU-05: cada usuario ve solo sus préstamos", s.prestamos().listarPrestamosUsuario(doc).isEmpty());
    }

    static void reglas() {
        titulo("Reglas de negocio transversales"); nuevo();
        verificar("RN-03: ninguna solicitud nace aprobada", registrar(est, 1, DIA, H8, H10).getEstado() == EstadoSolicitud.PENDIENTE);
        verificar("RN-07: el correo es único sin importar mayúsculas", s.usuarios().existeCorreo("ESTUDIANTE@itm.edu.co"));
        nuevo(); aprobada(est, 1, DIA, H8, H10); s.prestamos().registrarDevolucion(enc, prestamoDe(est));
        s.recursos().eliminarRecurso(enc, 1);
        verificar("RN-06: devuelto el recurso y sin otros trámites, ya se puede eliminar", s.recursos().consultarRecursos().stream().noneMatch(r -> r.getId() == 1));
        nuevo(); aprobada(est, 1, DIA, H8, H10); s.prestamos().registrarDevolucion(enc, prestamoDe(est));
        verificar("RN-01/RN-05: tras la devolución se libera el recurso y el usuario para ese mismo horario", registrar(doc, 1, DIA, H8, H10) != null && registrar(est, 2, DIA, H8, H10) != null);
    }

    static void rnf() {
        titulo("Requisitos no funcionales medibles"); nuevo();
        if (System.getenv("PRUEBAS_BD") != null) {   // contra PostgreSQL: los datos se cargan con SQL directo (si no, tardaría minutos)
            co.itm.prestamo.contrato.TablasPsql.psql("INSERT INTO recurso(tipo,nombre,estado,marca,modelo,numero_serie) SELECT 'EQUIPO','Equipo '||g,'DISPONIBLE','M','X','S'||g FROM generate_series(0,499) g");
            co.itm.prestamo.contrato.TablasPsql.psql("INSERT INTO solicitud(usuario_id,recurso_id,fecha,hora_inicio,hora_fin,motivo,estado,fecha_registro) SELECT 3, g+1, DATE '2026-11-20' + (g % 20), '08:00','10:00','m','APROBADA', now() FROM generate_series(0,299) g");
            co.itm.prestamo.contrato.TablasPsql.psql("INSERT INTO prestamo(solicitud_id,estado,fecha_creacion) SELECT id,'ACTIVO',now() FROM solicitud");
        } else {
            for (int i = 0; i < 500; i++) s.recursos().registrarRecurso(enc, "EQUIPO", Map.of("nombre", "Equipo " + i, "marca", "M", "modelo", "X", "numeroSerie", "S" + i));
            // 300 reservas: 15 solicitantes × 20 días (el encargado no solicita y RN-05 impide dos cruces del mismo usuario)
            List<Usuario> solicitantes = new ArrayList<>();
            for (int u = 0; u < 15; u++) solicitantes.add(s.usuarios().registrarUsuario(enc, "Solicitante " + u, "sol" + u + "@itm.edu.co", "Clave123", "ESTUDIANTE"));
            for (int i = 0; i < 300; i++) aprobadaSinFalla(solicitantes.get(i / 20), (i % 500) + 1, DIA.plusDays(i % 20));
        }
        long t0 = System.nanoTime(); int n = s.recursos().consultarDisponibles(DIA, H8, H10).size(); long ms = (System.nanoTime() - t0) / 1_000_000;
        verificar("RNF-03: la consulta de disponibilidad con 504 recursos y 300 reservas tarda menos de 3 s (" + ms + " ms)", ms < 3000 && n > 0);
    }
    static void aprobadaSinFalla(Usuario u, long recurso, LocalDate d) {
        try { Solicitud x = s.solicitudes().registrarSolicitud(u, pedir(recurso, d, H8, H10)); s.solicitudes().aprobarSolicitud(enc, x.getId()); } catch (NegocioException ignorada) { }
    }
}
