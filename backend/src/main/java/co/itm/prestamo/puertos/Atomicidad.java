package co.itm.prestamo.puertos;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * Ejecuta una operación que cambia datos de forma atómica: o se guarda todo o nada, y una operación a la vez.
 * En la aplicación real es una transacción de PostgreSQL con el mismo candado asesor que usan las solicitudes
 * (spring/Configuracion), de modo que, por ejemplo, eliminar un recurso no se cruza con el registro de una solicitud (RN-06).
 * Los servicios no conocen a Spring ni a SQL: solo esta interfaz.
 */
public interface Atomicidad {
    <T> T ejecutar(Supplier<T> trabajo);

    /** Versión para las pruebas con tablas en memoria: un candado de proceso. */
    static Atomicidad conCandado() {
        ReentrantLock candado = new ReentrantLock();
        return new Atomicidad() {
            @Override public <T> T ejecutar(Supplier<T> trabajo) {
                candado.lock();
                try { return trabajo.get(); } finally { candado.unlock(); }
            }
        };
    }
}
