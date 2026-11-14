package co.itm.prestamo.solicitudes;

import co.itm.prestamo.dominio.Solicitud;

/** Patrón Observer: se avisa a cada observador cuando el encargado decide una solicitud. */
public interface SolicitudObserver { void solicitudDecidida(Solicitud solicitud); }
