package co.itm.prestamo;

import co.itm.prestamo.disponibilidad.DisponibilidadService;
import co.itm.prestamo.dominio.*;
import co.itm.prestamo.inventario.*;
import co.itm.prestamo.persistencia.Tablas;
import co.itm.prestamo.prestamos.*;
import co.itm.prestamo.puertos.Atomicidad;
import co.itm.prestamo.solicitudes.*;
import co.itm.prestamo.usuarios.*;
import java.time.Clock;
import java.util.LinkedHashMap;
import java.util.Map;

/** Raíz de composición: arma los servicios, los patrones y los repositorios (inyección de dependencias manual). La aplicación real la invoca desde spring/Configuracion con tablas de PostgreSQL. */
public class Main {
    /** Raíz de composición: recibe las tablas (PostgreSQL en la aplicación real) y conecta servicios, patrones y repositorios. */
    public static Sistema armar(Clock reloj, Tablas t) { return armar(reloj, t, Atomicidad.conCandado()); }

    /** Igual, con la atomicidad de la aplicación real (transacción de PostgreSQL con candado asesor; ver spring/Configuracion). */
    public static Sistema armar(Clock reloj, Tablas t, Atomicidad atomicidad) {
        UsuarioRepository usuarioRepo = new UsuarioRepository(t.usuarios());
        UsuarioService usuarios = new UsuarioService(usuarioRepo, atomicidad);
        SesionService sesiones = new SesionService();
        RecursoRepository recursoRepo = new RecursoRepository(t.recursos());
        SolicitudRepository solicitudRepo = new SolicitudRepository(t.solicitudes(), usuarioRepo, recursoRepo);
        PrestamoRepository prestamoRepo = new PrestamoRepository(t.prestamos(), solicitudRepo);
        PrestamoService prestamosReal = new PrestamoService(prestamoRepo, recursoRepo, reloj);
        NotificacionService notificaciones = new NotificacionService(new NotificacionRepository(t.notificaciones()), reloj, atomicidad);
        DisponibilidadService disponibilidad = new DisponibilidadService(solicitudRepo, prestamosReal, reloj);

        // Chain of Responsibility: las dos cadenas se arman aquí y se inyectan (DIP)
        ValidadorSolicitud registro = new ValidadorCampos();
        registro.enlazar(new ValidadorHorario(reloj)).enlazar(new ValidadorConflictoRecurso(disponibilidad)).enlazar(new ValidadorConflictoUsuario(disponibilidad));
        ValidadorSolicitud aprobacion = new ValidadorHorario(reloj);
        aprobacion.enlazar(new ValidadorConflictoRecurso(disponibilidad)).enlazar(new ValidadorConflictoUsuario(disponibilidad));

        SolicitudService solicitudReal = new SolicitudService(solicitudRepo, recursoRepo, registro, aprobacion, reloj);
        solicitudReal.agregarObservador(new NotificadorSolicitante(notificaciones));   // Observer
        solicitudReal.agregarObservador(new CreadorDePrestamo(prestamosReal));
        SolicitudOperaciones solicitudes = new SolicitudServiceProxy(solicitudReal, usuarios);   // Proxy
        PrestamoOperaciones prestamos = new PrestamoServiceProxy(prestamosReal, usuarios);       // Proxy

        Map<TipoRecurso, RecursoCreator> creadores = new LinkedHashMap<>();                       // Factory Method
        creadores.put(TipoRecurso.LABORATORIO, new LaboratorioCreator());
        creadores.put(TipoRecurso.EQUIPO, new EquipoCreator());
        RecursoService recursos = new RecursoService(recursoRepo, disponibilidad, disponibilidad, usuarios, creadores, atomicidad);

        return new Sistema(usuarios, sesiones, solicitudes, prestamos, recursos, notificaciones);
    }

    /** Cuentas y recursos de demostración (no son datos reales de la institución). */
    public static void cargarDatosDeDemostracion(UsuarioService usuarios, RecursoService recursos) {
        usuarios.crearCuentaInicial("Estudiante de prueba", "estudiante@itm.edu.co", "Estudiante123", RolUsuario.ESTUDIANTE);
        usuarios.crearCuentaInicial("Docente de prueba", "docente@itm.edu.co", "Docente123", RolUsuario.DOCENTE);
        Usuario encargado = usuarios.crearCuentaInicial("Encargado de prueba", "encargado@itm.edu.co", "Encargado123", RolUsuario.ENCARGADO);
        recursos.registrarRecurso(encargado, "LABORATORIO", Map.of("nombre", "Laboratorio de Redes", "ubicacion", "Bloque K, piso 2", "capacidad", "30"));
        recursos.registrarRecurso(encargado, "LABORATORIO", Map.of("nombre", "Laboratorio de Robótica", "ubicacion", "Bloque M, piso 1", "capacidad", "20"));
        recursos.registrarRecurso(encargado, "EQUIPO", Map.of("nombre", "Impresora 3D", "marca", "Creality", "modelo", "Ender 3", "numeroSerie", "IMP-3D-01"));
        recursos.registrarRecurso(encargado, "EQUIPO", Map.of("nombre", "Gafas de realidad virtual", "marca", "Meta", "modelo", "Quest 2", "numeroSerie", "VR-02"));
    }

    public record Sistema(UsuarioService usuarios, SesionService sesiones, SolicitudOperaciones solicitudes,
                          PrestamoOperaciones prestamos, RecursoService recursos, NotificacionService notificaciones) {
    }

}
