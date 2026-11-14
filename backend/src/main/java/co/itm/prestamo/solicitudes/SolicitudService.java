package co.itm.prestamo.solicitudes;

import co.itm.prestamo.dominio.*;
import co.itm.prestamo.excepciones.*;
import co.itm.prestamo.inventario.RecursoRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Servicio real de Gestión de Solicitudes. Es el sujeto del Observer y el cliente de la Chain of Responsibility.
 * Las cadenas llegan inyectadas (DIP). Un candado hace de «transacción» (RNF-05): las operaciones se procesan una tras otra.
 */
public class SolicitudService implements SolicitudOperaciones {
    private final SolicitudRepository solicitudes;
    private final RecursoRepository recursos;
    private final ValidadorSolicitud cadenaRegistro;   // campos → horario → conflicto de recurso → conflicto de usuario
    private final ValidadorSolicitud cadenaAprobacion; // horario (no vencido) → conflicto de recurso → conflicto de usuario
    private final Clock reloj;
    private final List<SolicitudObserver> observadores = new ArrayList<>();
    private final ReentrantLock transaccion = new ReentrantLock();

    public SolicitudService(SolicitudRepository solicitudes, RecursoRepository recursos,
                            ValidadorSolicitud cadenaRegistro, ValidadorSolicitud cadenaAprobacion, Clock reloj) {
        this.solicitudes = solicitudes; this.recursos = recursos;
        this.cadenaRegistro = cadenaRegistro; this.cadenaAprobacion = cadenaAprobacion; this.reloj = reloj;
    }

    public void agregarObservador(SolicitudObserver o) { observadores.add(o); }
    public void quitarObservador(SolicitudObserver o) { observadores.remove(o); }
    private void notificarObservadores(Solicitud s) { for (SolicitudObserver o : observadores) o.solicitudDecidida(s); }

    @Override public Solicitud registrarSolicitud(Usuario actor, NuevaSolicitud d) {
        transaccion.lock();
        try {
            Recurso recurso = d.recursoId() == null ? null
                : recursos.buscar(d.recursoId()).orElseThrow(() -> new RecursoNoEncontradoException("El recurso no existe"));
            Solicitud s = new Solicitud(actor, recurso, d.fecha(), d.horaInicio(), d.horaFin(), d.motivo(), LocalDateTime.now(reloj));
            cadenaRegistro.validar(s);          // si una regla falla, lanza excepción y no se guarda
            return solicitudes.guardar(s);      // queda en estado «Pendiente» (RN-03)
        } finally { transaccion.unlock(); }
    }

    @Override public Solicitud aprobarSolicitud(Usuario actor, long id) {
        transaccion.lock();
        try {
            Solicitud s = buscar(id);
            // Solo una pendiente se valida; si ya fue decidida, el estado (State) rechaza la transición con su propio mensaje
            if (s.estaPendiente()) cadenaAprobacion.validar(s);   // horario vigente y conflictos con lo aprobado (RN-01, RN-04, RN-05)
            s.aprobar();                        // State: solo «Pendiente» permite la transición
            solicitudes.guardar(s);
            notificarObservadores(s);           // Observer: aviso al solicitante y creación del préstamo
            return s;
        } finally { transaccion.unlock(); }
    }

    @Override public Solicitud rechazarSolicitud(Usuario actor, long id) {
        transaccion.lock();
        try {
            Solicitud s = buscar(id);
            s.rechazar();
            solicitudes.guardar(s);
            notificarObservadores(s);
            return s;
        } finally { transaccion.unlock(); }
    }

    @Override public List<Solicitud> listarPendientes(Usuario actor) {
        List<Solicitud> r = new ArrayList<>();
        for (Solicitud s : solicitudes.todas()) if (s.estaPendiente()) r.add(s);   // orden de llegada
        return r;
    }

    @Override public List<Solicitud> listarDeUsuario(Usuario actor) {
        List<Solicitud> r = new ArrayList<>();
        for (Solicitud s : solicitudes.todas()) if (s.getSolicitante().getId() == actor.getId()) r.add(s);
        return r;
    }

    private Solicitud buscar(long id) {
        return solicitudes.buscar(id).orElseThrow(() -> new RecursoNoEncontradoException("La solicitud no existe"));
    }
}
