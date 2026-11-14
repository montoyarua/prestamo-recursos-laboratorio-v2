package co.itm.prestamo.solicitudes;

import co.itm.prestamo.dominio.Solicitud;
import co.itm.prestamo.puertos.PrestamoPort;

/** Si la solicitud fue aprobada, crea el préstamo activo. */
public class CreadorDePrestamo implements SolicitudObserver {
    private final PrestamoPort prestamos;
    public CreadorDePrestamo(PrestamoPort prestamos) { this.prestamos = prestamos; }
    @Override public void solicitudDecidida(Solicitud s) { if (s.estaAprobada()) prestamos.crearPrestamo(s); }
}
