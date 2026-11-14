package co.itm.prestamo.persistencia;

/** SOLO PARA PRUEBAS: las cinco tablas en memoria. La aplicación real usa únicamente PostgreSQL (persistencia/jdbc). */
public final class TablasMemoria {
    private TablasMemoria() { }
    public static Tablas crear() {
        return new Tablas(new TablaMemoria<>(), new TablaMemoria<>(), new TablaMemoria<>(), new TablaMemoria<>(), new TablaMemoria<>());
    }
}
