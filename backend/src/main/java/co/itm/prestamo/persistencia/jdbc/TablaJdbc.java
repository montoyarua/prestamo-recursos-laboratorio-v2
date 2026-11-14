package co.itm.prestamo.persistencia.jdbc;

import co.itm.prestamo.persistencia.Fila;
import co.itm.prestamo.persistencia.Tabla;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * Tabla de PostgreSQL con Spring JDBC. Cada subclase solo dice su nombre, sus columnas (sin el id), cómo leer una fila
 * y qué valores y tipos SQL tiene cada columna; aquí se arman las sentencias INSERT, UPDATE y SELECT.
 */
abstract class TablaJdbc<F extends Fila<F>> implements Tabla<F> {
    protected final JdbcTemplate jdbc;
    protected TablaJdbc(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    protected abstract String tabla();
    protected abstract String[] columnas();          // sin el id
    protected abstract RowMapper<F> mapeo();
    protected abstract Object[] valores(F fila);     // en el mismo orden que columnas()
    protected abstract int[] tipos();                // java.sql.Types, en el mismo orden que columnas()

    @Override public F guardar(F fila) {
        String[] cols = columnas();
        if (fila.id() == 0) {
            String marcas = String.join(", ", Arrays.stream(cols).map(c -> "?").toArray(String[]::new));
            String sql = "INSERT INTO " + tabla() + " (" + String.join(", ", cols) + ") VALUES (" + marcas + ") RETURNING id";
            Long id = jdbc.queryForObject(sql, valores(fila), tipos(), Long.class);
            return fila.conId(id);
        }
        String asignaciones = String.join(", ", Arrays.stream(cols).map(c -> c + " = ?").toArray(String[]::new));
        Object[] args = Arrays.copyOf(valores(fila), cols.length + 1); args[cols.length] = fila.id();
        int[] tp = Arrays.copyOf(tipos(), cols.length + 1); tp[cols.length] = Types.BIGINT;
        jdbc.update("UPDATE " + tabla() + " SET " + asignaciones + " WHERE id = ?", args, tp);
        return fila;
    }

    @Override public Optional<F> buscar(long id) {
        List<F> r = jdbc.query("SELECT * FROM " + tabla() + " WHERE id = ?", mapeo(), id);
        return r.isEmpty() ? Optional.empty() : Optional.of(r.get(0));
    }

    @Override public List<F> todas() { return jdbc.query("SELECT * FROM " + tabla() + " ORDER BY id", mapeo()); }
}
