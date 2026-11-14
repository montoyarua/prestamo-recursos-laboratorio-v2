package co.itm.prestamo.solicitudes;

import co.itm.prestamo.dominio.Solicitud;

/** Patrón Chain of Responsibility: cada validador revisa una regla y pasa la solicitud al siguiente. */
public abstract class ValidadorSolicitud {
    private ValidadorSolicitud siguiente;
    public ValidadorSolicitud enlazar(ValidadorSolicitud siguiente) { this.siguiente = siguiente; return siguiente; }
    public final void validar(Solicitud solicitud) {
        comprobar(solicitud);                       // lanza una excepción si la regla falla
        if (siguiente != null) siguiente.validar(solicitud);
    }
    protected abstract void comprobar(Solicitud solicitud);
}
