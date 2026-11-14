package co.itm.prestamo.persistencia;

/** Una fila de una tabla: valores simples (texto, números, fechas), sin referencias entre objetos. El id 0 significa «aún no guardada». */
public interface Fila<F extends Fila<F>> {
    long id();
    F conId(long nuevoId);
}
