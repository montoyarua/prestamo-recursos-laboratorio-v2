package co.itm.prestamo.spring;

import co.itm.prestamo.Main;
import co.itm.prestamo.Main.Sistema;
import co.itm.prestamo.persistencia.Tablas;
import co.itm.prestamo.puertos.Atomicidad;
import co.itm.prestamo.persistencia.jdbc.TablasJdbc;
import co.itm.prestamo.prestamos.NotificacionService;
import co.itm.prestamo.prestamos.PrestamoOperaciones;
import co.itm.prestamo.inventario.RecursoService;
import co.itm.prestamo.solicitudes.SolicitudOperaciones;
import co.itm.prestamo.usuarios.SesionService;
import co.itm.prestamo.usuarios.UsuarioService;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Raíz de composición de la aplicación real: crea el esquema en PostgreSQL, arma el sistema con las tablas JDBC (Main.armar) y
 * envuelve en transacciones las operaciones que cambian datos: solicitudes y préstamos (RNF-05), inventario, usuarios y avisos.
 */
@Configuration
public class Configuracion {

    @Bean
    public Clock reloj() { return Clock.systemDefaultZone(); }

    @Bean
    public Sistema sistema(Clock reloj, JdbcTemplate jdbc, PlatformTransactionManager gestor,
                           @Value("${prestamo.datos-demostracion:true}") boolean datosDemostracion) {
        crearEsquema(jdbc);
        Tablas tablas = TablasJdbc.crear(jdbc);
        TransactionTemplate transaccion = new TransactionTemplate(gestor);
        // Inventario, usuarios y avisos: transacción con el mismo candado asesor que las solicitudes (RN-06, RN-07)
        Atomicidad atomicidad = new Atomicidad() {
            @Override public <T> T ejecutar(Supplier<T> trabajo) {
                return transaccion.execute(estado -> {
                    jdbc.execute("SELECT pg_advisory_xact_lock(" + SolicitudesTransaccionales.BLOQUEO_SOLICITUDES + ")");
                    return trabajo.get();
                });
            }
        };
        Sistema base = Main.armar(reloj, tablas, atomicidad);
        if (datosDemostracion && !base.usuarios().hayUsuarios()) Main.cargarDatosDeDemostracion(base.usuarios(), base.recursos());
        return new Sistema(base.usuarios(), base.sesiones(),
            new SolicitudesTransaccionales(base.solicitudes(), transaccion, jdbc),
            new PrestamosTransaccionales(base.prestamos(), transaccion, jdbc),
            base.recursos(), base.notificaciones());
    }

    @Bean public UsuarioService usuarioService(Sistema s) { return s.usuarios(); }
    @Bean public SesionService sesionService(Sistema s) { return s.sesiones(); }
    @Bean public SolicitudOperaciones solicitudOperaciones(Sistema s) { return s.solicitudes(); }
    @Bean public PrestamoOperaciones prestamoOperaciones(Sistema s) { return s.prestamos(); }
    @Bean public RecursoService recursoService(Sistema s) { return s.recursos(); }
    @Bean public NotificacionService notificacionService(Sistema s) { return s.notificaciones(); }

    /** Ejecuta base_de_datos.sql (es idempotente: solo crea lo que falta). */
    private static void crearEsquema(JdbcTemplate jdbc) {
        try (InputStream in = new ClassPathResource("base_de_datos.sql").getInputStream()) {
            jdbc.execute(new String(in.readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer base_de_datos.sql", e);
        }
    }
}
