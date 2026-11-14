package co.itm.prestamo.contrato;

import co.itm.prestamo.api.Vistas;
import co.itm.prestamo.api.Convertir;

import co.itm.prestamo.excepciones.RecursoNoEncontradoException;
import co.itm.prestamo.prestamos.PrestamoOperaciones;

/** HU-05 y HU-08: /api/prestamos/mios, /api/prestamos/activos, POST /api/prestamos/{id}/devolver. */
public class PrestamoController implements Controlador {
    private final PrestamoOperaciones prestamos;
    public PrestamoController(PrestamoOperaciones prestamos) { this.prestamos = prestamos; }
    @Override public Object manejar(Peticion p) {
        if (p.es("GET", "mios")) return Vistas.lista(prestamos.listarPrestamosUsuario(p.actor()), Vistas::prestamo);
        if (p.es("GET", "activos")) return Vistas.lista(prestamos.listarPrestamosActivos(p.actor()), Vistas::prestamo);
        if (p.es("POST", 2) && p.partes().get(1).equals("devolver")) return Vistas.prestamo(prestamos.registrarDevolucion(p.actor(), p.idEn(0)));
        throw new RecursoNoEncontradoException("Ruta no encontrada");
    }
}
