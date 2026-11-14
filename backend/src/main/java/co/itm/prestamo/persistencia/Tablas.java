package co.itm.prestamo.persistencia;

import co.itm.prestamo.persistencia.Filas.*;

/** Las cinco tablas del sistema. La raíz de composición recibe estas tablas: PostgreSQL (TablasJdbc, aplicación real); las pruebas rápidas usan una versión en memoria que vive solo en src/test. */
public record Tablas(Tabla<UsuarioFila> usuarios, Tabla<RecursoFila> recursos, Tabla<SolicitudFila> solicitudes,
                     Tabla<PrestamoFila> prestamos, Tabla<NotificacionFila> notificaciones) {
}
