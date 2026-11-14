package co.itm.prestamo.persistencia;

import java.util.*;

/** Tabla en memoria. Guarda filas (no objetos del dominio), igual que una base de datos: cada lectura reconstruye los objetos. */
public class TablaMemoria<F extends Fila<F>> implements Tabla<F> {
    private final Map<Long, F> datos = new TreeMap<>();
    private long secuencia = 0;
    @Override public synchronized F guardar(F f) {
        F g = f.id() == 0 ? f.conId(++secuencia) : f;
        datos.put(g.id(), g); return g;
    }
    @Override public synchronized Optional<F> buscar(long id) { return Optional.ofNullable(datos.get(id)); }
    @Override public synchronized List<F> todas() { return new ArrayList<>(datos.values()); }
}
