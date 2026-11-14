package co.itm.prestamo.persistencia;

import java.util.List;
import java.util.Optional;

/**
 * Acceso a una tabla. Hay dos implementaciones: en memoria (pruebas automáticas) y en PostgreSQL con Spring JDBC (aplicación real).
 * Los repositorios del dominio solo dependen de esta interfaz.
 */
public interface Tabla<F extends Fila<F>> {
    /** Inserta si el id es 0 (y devuelve la fila con su id nuevo) o actualiza si ya existe. */
    F guardar(F fila);
    Optional<F> buscar(long id);
    /** Todas las filas, en orden de creación (por id). */
    List<F> todas();
}
