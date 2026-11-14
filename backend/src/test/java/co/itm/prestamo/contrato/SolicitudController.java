package co.itm.prestamo.contrato;

import co.itm.prestamo.api.Vistas;
import co.itm.prestamo.api.Convertir;

import co.itm.prestamo.excepciones.*;
import co.itm.prestamo.solicitudes.*;
import java.util.Map;

/** HU-02, HU-03, HU-07: /api/solicitudes (POST), /pendientes, /mias, /{id}/aprobar, /{id}/rechazar. */
public class SolicitudController implements Controlador {
    private final SolicitudOperaciones solicitudes;
    public SolicitudController(SolicitudOperaciones solicitudes) { this.solicitudes = solicitudes; }
    @Override public Object manejar(Peticion p) {
        if (p.es("POST", 0)) {
            Map<String, String> d = p.cuerpo();
            Long recurso = null;
            if (d.get("recursoId") != null && !d.get("recursoId").isBlank()) {
                try { recurso = Long.parseLong(d.get("recursoId").trim()); } catch (NumberFormatException e) { throw new CamposIncompletosException("Recurso no válido"); }
            }
            return Vistas.solicitud(solicitudes.registrarSolicitud(p.actor(),
                new NuevaSolicitud(recurso, Convertir.fecha(d.get("fecha")), Convertir.hora(d.get("horaInicio")), Convertir.hora(d.get("horaFin")), d.get("motivo"))));
        }
        if (p.es("GET", "pendientes")) return Vistas.lista(solicitudes.listarPendientes(p.actor()), Vistas::solicitud);
        if (p.es("GET", "mias")) return Vistas.lista(solicitudes.listarDeUsuario(p.actor()), Vistas::solicitud);
        if (p.es("POST", 2) && p.partes().get(1).equals("aprobar")) return Vistas.solicitud(solicitudes.aprobarSolicitud(p.actor(), p.idEn(0)));
        if (p.es("POST", 2) && p.partes().get(1).equals("rechazar")) return Vistas.solicitud(solicitudes.rechazarSolicitud(p.actor(), p.idEn(0)));
        throw new RecursoNoEncontradoException("Ruta no encontrada");
    }
}
