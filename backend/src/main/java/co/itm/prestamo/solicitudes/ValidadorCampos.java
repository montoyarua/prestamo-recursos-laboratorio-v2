package co.itm.prestamo.solicitudes;

import co.itm.prestamo.dominio.Solicitud;
import co.itm.prestamo.excepciones.CamposIncompletosException;
import java.util.ArrayList;
import java.util.List;

public class ValidadorCampos extends ValidadorSolicitud {
    @Override protected void comprobar(Solicitud s) {
        List<String> faltan = new ArrayList<>();
        if (s.getRecurso() == null) faltan.add("recurso");
        if (s.getFecha() == null) faltan.add("fecha");
        if (s.getHoraInicio() == null) faltan.add("hora de inicio");
        if (s.getHoraFin() == null) faltan.add("hora de fin");
        if (s.getMotivo() == null || s.getMotivo().isBlank()) faltan.add("motivo");
        if (!faltan.isEmpty()) throw new CamposIncompletosException("Faltan campos obligatorios: " + String.join(", ", faltan));
    }
}
